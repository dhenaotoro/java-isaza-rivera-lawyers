const path = require('path');
const cdk = require('aws-cdk-lib');
const ec2 = require('aws-cdk-lib/aws-ec2');
const ecs = require('aws-cdk-lib/aws-ecs');
const ecsPatterns = require('aws-cdk-lib/aws-ecs-patterns');
const rds = require('aws-cdk-lib/aws-rds');

class LegacyAdviceApiStack extends cdk.Stack {
  constructor(scope, id, props) {
    super(scope, id, props);

    const dbName = new cdk.CfnParameter(this, 'DatabaseName', {
      type: 'String',
      default: 'legaldb'
    });

    const appReportsLeadsCron = new cdk.CfnParameter(this, 'AppReportsLeadsCron', {
      type: 'String',
      default: '0 0 18 * * *',
      description: 'Cron expression for lead report scheduler'
    });

    const springMailHost = new cdk.CfnParameter(this, 'SpringMailHost', {
      type: 'String',
      default: '',
      description: 'SMTP host (e.g. smtp.gmail.com)'
    });

    const springMailPort = new cdk.CfnParameter(this, 'SpringMailPort', {
      type: 'String',
      default: '587',
      description: 'SMTP port'
    });

    const springMailUsername = new cdk.CfnParameter(this, 'SpringMailUsername', {
      type: 'String',
      default: '',
      description: 'SMTP username / sender email'
    });

    const springMailPassword = new cdk.CfnParameter(this, 'SpringMailPassword', {
      type: 'String',
      noEcho: true,
      default: '',
      description: 'SMTP password or app password'
    });

    const vpc = new ec2.Vpc(this, 'ApiVpc', {
      maxAzs: 2,
      natGateways: 1
    });

    const cluster = new ecs.Cluster(this, 'ApiCluster', {
      vpc,
      containerInsightsV2: ecs.ContainerInsights.ENABLED
    });

    const database = new rds.DatabaseInstance(this, 'ApiMySql', {
      vpc,
      vpcSubnets: { subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS },
      engine: rds.DatabaseInstanceEngine.mysql({
        version: rds.MysqlEngineVersion.VER_8_0_39
      }),
      credentials: rds.Credentials.fromGeneratedSecret('legal_user'),
      databaseName: dbName.valueAsString,
      instanceType: ec2.InstanceType.of(ec2.InstanceClass.T3, ec2.InstanceSize.MICRO),
      allocatedStorage: 20,
      maxAllocatedStorage: 100,
      multiAz: false,
      publiclyAccessible: false,
      deletionProtection: true,
      removalPolicy: cdk.RemovalPolicy.RETAIN,
      backupRetention: cdk.Duration.days(7)
    });

    const fargateService = new ecsPatterns.ApplicationLoadBalancedFargateService(this, 'ApiService', {
      cluster,
      publicLoadBalancer: true,
      desiredCount: 1,
      cpu: 512,
      memoryLimitMiB: 1024,
      taskImageOptions: {
        image: ecs.ContainerImage.fromAsset(path.join(__dirname, '..', '..'), {
          ignoreMode: cdk.IgnoreMode.GLOB,
          exclude: [
            'infra/cdk.out/**',
            'infra/node_modules/**',
            '.git/**',
            '.gradle/**',
            'build/**'
          ]
        }),
        containerPort: 8081,
        environment: {
          SPRING_PROFILES_ACTIVE: 'docker',
          SPRING_DATASOURCE_URL: `jdbc:mysql://${database.instanceEndpoint.hostname}:3306/${dbName.valueAsString}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`,
          SPRING_DATASOURCE_USERNAME: 'legal_user',
          SPRING_JPA_HIBERNATE_DDL_AUTO: 'validate',
          APP_REPORTS_LEADS_CRON: appReportsLeadsCron.valueAsString,
          SPRING_MAIL_HOST: springMailHost.valueAsString,
          SPRING_MAIL_PORT: springMailPort.valueAsString,
          SPRING_MAIL_USERNAME: springMailUsername.valueAsString,
          SPRING_MAIL_PASSWORD: springMailPassword.valueAsString
        },
        secrets: {
          SPRING_DATASOURCE_PASSWORD: ecs.Secret.fromSecretsManager(database.secret, 'password')
        },
        logDriver: ecs.LogDrivers.awsLogs({
          streamPrefix: 'legacy-advice-api'
        })
      }
    });

    database.connections.allowDefaultPortFrom(fargateService.service, 'Allow ECS tasks to connect to RDS');

    fargateService.targetGroup.configureHealthCheck({
      path: '/actuator/health/readiness',
      healthyHttpCodes: '200'
    });

    new cdk.CfnOutput(this, 'LoadBalancerUrl', {
      value: `http://${fargateService.loadBalancer.loadBalancerDnsName}`
    });

    new cdk.CfnOutput(this, 'DatabaseEndpoint', {
      value: database.instanceEndpoint.hostname
    });

    new cdk.CfnOutput(this, 'DatabaseSecretArn', {
      value: database.secret.secretArn
    });
  }
}

module.exports = { LegacyAdviceApiStack };
