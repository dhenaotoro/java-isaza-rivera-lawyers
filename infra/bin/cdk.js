#!/usr/bin/env node
const cdk = require('aws-cdk-lib');
const { LegacyAdviceApiStack } = require('../lib/legacy-advice-api-stack');

const app = new cdk.App();

new LegacyAdviceApiStack(app, 'LegacyAdviceApiStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION || 'us-east-1'
  }
});
