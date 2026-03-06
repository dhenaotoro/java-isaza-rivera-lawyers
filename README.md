# java-isaza-rivera-lawyers

## Resumen de endpoints

- `POST /api/v1/leads` → crea un lead.
- `POST /api/v1/leads/{id}/confirm` → confirma el lead (cambia estado a `CONFIRMED_APPOINTMENT`).

## Reporte automático diario de leads

La app ejecuta un job programado todos los días a las **6:00 PM** (zona `America/Bogota`) y exporta la tabla de leads a CSV con estas columnas:

- `name`
- `city`
- `request_type`
- `description`
- `email`
- `cellphone`

Destino del reporte:

- WhatsApp: `+573108216768`
- Email: `leslierivera.2503@gmail.com`

Configuración en `application.yml` / `application-docker.yml`:

```yaml
app:
	reports:
		leads:
			cron: "0 0 18 * * *"
			zone: "America/Bogota"
			whatsapp-recipient: "+573108216768"
			email-recipient: "leslierivera.2503@gmail.com"
			email-subject: "Reporte diario de leads"
			email-from: "no-reply@isazariveralawyers.com"
```

Para envío por correo, configura SMTP por variables de entorno:

```bash
SPRING_MAIL_HOST=
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_STARTTLS_ENABLE=true
```

Nota: el envío a WhatsApp está implementado con un servicio `stub`; para producción debes conectar `WhatsappService` con WhatsApp Business Cloud API para enviar el archivo real.

---

## Ejecutar con Podman Compose (recomendado)

Prerequisitos: `podman`, `podman-compose` y que la `podman machine` esté iniciada.

1. Crear archivo `.env` en la raíz del proyecto (Podman Compose lo carga automáticamente):

```bash
cat > .env <<'EOF'
# Scheduler (ajústalo a 0 0 18 * * * para diario 6:00 PM)
APP_REPORTS_LEADS_CRON=0 * * * * *

# SMTP (Gmail ejemplo)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu_correo@gmail.com
SPRING_MAIL_PASSWORD=tu_app_password_16_chars
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_STARTTLS_ENABLE=true
EOF
```

Variables necesarias para envío por email:

- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
- `SPRING_MAIL_SMTP_AUTH`
- `SPRING_MAIL_STARTTLS_ENABLE`

Variable opcional para frecuencia del job:

- `APP_REPORTS_LEADS_CRON` (por ejemplo `0 * * * * *` cada minuto)

2. Iniciar la máquina de Podman (si no está creada):

```bash
podman machine init
podman machine start
```

3. Desde la raíz del proyecto construir y levantar los servicios:

```bash
podman-compose build --no-cache api
podman-compose up -d
```

4. Ver logs:

```bash
podman-compose logs -f api
podman-compose logs -f mysql
```

5. Parar y eliminar:

```bash
podman-compose down -v
```

La API escucha en `http://localhost:8081` (puerto mapeado por `podman-compose.yml`).

Notas:

- Si editas `.env`, recrea API para aplicar cambios: `podman-compose up -d --force-recreate api`.
- No subas `.env` al repositorio (está ignorado por `.gitignore`).

---

## Ejecutar localmente con Gradle (alternativa rápida)

Si prefieres ejecutar sin contenedores:

```bash
./gradlew bootRun --args='--spring.profiles.active=docker'
```

Nota: la profile `docker` carga `src/app/main/resources/application-docker.yml` que usa MySQL. Para ejecución sin MySQL, borra/ajusta la URL en `application.yml`.

---

## Despliegue en AWS con CDK (JavaScript)

El proyecto CDK ubicado en `infra/` sirve para desplegar:

- API en **ECS Fargate**
- **Application Load Balancer** público
- Base de datos **RDS MySQL** privada

### Requisitos

- Node.js 18+
- AWS CLI configurado (`aws configure`)
- credenciales con permisos para ECS, ALB, VPC, RDS, IAM, CloudFormation, ECR

### Instalar CDK y dependencias

```bash
cd infra
npm install
```

### Bootstrap de la cuenta/región (una sola vez)

```bash
npx cdk bootstrap aws://<AWS_ACCOUNT_ID>/<AWS_REGION>
```

### Sintetizar y desplegar

```bash
npm run synth
npm run deploy -- \
	--parameters SpringMailHost=smtp.gmail.com \
	--parameters SpringMailPort=587 \
	--parameters SpringMailUsername=tu_correo@gmail.com \
	--parameters SpringMailPassword=tu_app_password \
	--parameters AppReportsLeadsCron='0 0 18 * * *'
```

El stack publica outputs con:

- URL del Load Balancer
- Endpoint de la base de datos
- ARN del secret de RDS

### Limpiar recursos

```bash
npm run destroy
```

> Nota: el stack CDK construye la imagen Docker usando el `Dockerfile` de la raíz del proyecto. `podman-compose` sigue siendo para entorno local.

---

## Probar con Postman / curl

Endpoint para crear un lead:

```
POST http://localhost:8081/api/v1/leads
Content-Type: application/json
```

Ejemplo de cuerpo JSON (use `RequestType` como DIVORCED | CHILD_SUPPORT | CUSTODY | DOMESTIC_VIOLENCE | OTHER):

```json
{
	"firstName": "Ana",
	"lastName": "Perez",
	"city": "Bogota",
	"email": "ana@example.com",
	"phone": "3001234567",
	"summary": "Necesito asesoría sobre divorcio",
	"requestType": "DIVORCED",
	"hasMinors": false,
	"dataProcessingConsent": true,
	"whatsappConsent": true,
	"source": "instagram"
}
```

curl equivalente:

```bash
curl -X POST http://localhost:8081/api/v1/leads \
	-H 'Content-Type: application/json' \
	-d '{"firstName":"Ana","lastName":"Perez","city":"Bogota","email":"ana@example.com","phone":"3001234567","summary":"Necesito asesoría","requestType":"DIVORCED","whatsappConsent":true}'
```

Para confirmar un lead (ejemplo id 1):

```bash
curl -X POST http://localhost:8081/api/v1/leads/1/confirm
```

Respuesta esperada:

```text
Lead confirmed successfully
```

---

## Verificar la base de datos (MySQL)

Si `podman-compose` mapea el puerto 3306, con un cliente MySQL:

```bash
mysql -h 127.0.0.1 -P 3306 -u legal_user -p
# contraseña: legal_password
USE legaldb;
SHOW TABLES;
DESCRIBE `lead`;
SELECT * FROM `lead` LIMIT 10;
```

---

## Migraciones

Flyway está incluido; las migraciones están en `src/app/main/resources/db/migration`. Al arrancar la app se aplican automáticamente.

---

## Notas rápidas

- Si `podman-compose` no está disponible, instale `pipx` y luego `pipx install podman-compose`, o use Homebrew: `brew install podman`.
- Si ves `zsh: command not found: pipx`, añade `export PATH="$HOME/.local/bin:$PATH"` a `~/.zshrc` y reinicia el shell.
- Si la API no arranca, verifica `podman-compose logs api` para errores (Flyway, conexión MySQL, variables de entorno).