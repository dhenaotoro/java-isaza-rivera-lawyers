# java-isaza-rivera-lawyers

## Resumen de endpoints

- `POST /api/v1/leads` → crea un lead.
- `GET /api/v1/leads/{id}` → obtiene un lead.
- `POST /api/v1/leads/{id}/confirm` → confirma cita/estado del lead.
- `POST /api/v1/payments/checkout` → genera link de pago (stub en esta plantilla).
- `POST /webhooks/payments/{provider}` → recibe webhooks de pasarelas.

---

## Ejecutar con Podman Compose (recomendado)

Prerequisitos: `podman`, `podman-compose` y que la `podman machine` esté iniciada.

1. Iniciar la máquina de Podman (si no está creada):

```bash
podman machine init
podman machine start
```

2. Desde la raíz del proyecto construir y levantar los servicios:

```bash
podman-compose build --no-cache api
podman-compose up -d
```

3. Ver logs:

```bash
podman-compose logs -f api
podman-compose logs -f mysql
```

4. Parar y eliminar:

```bash
podman-compose down -v
```

La API escucha en `http://localhost:8081` (puerto mapeado por `podman-compose.yml`).

---

## Ejecutar localmente con Gradle (alternativa rápida)

Si prefieres ejecutar sin contenedores:

```bash
./gradlew bootRun --args='--spring.profiles.active=docker'
```

Nota: la profile `docker` carga `src/app/main/resources/application-docker.yml` que usa MySQL. Para ejecución sin MySQL, borra/ajusta la URL en `application.yml`.

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

Si quieres, puedo:
- levantar los contenedores y comprobar los logs, o
- ejecutar un ejemplo `curl` para crear un lead desde aquí.

Gracias — dime qué prefieres que haga ahora.