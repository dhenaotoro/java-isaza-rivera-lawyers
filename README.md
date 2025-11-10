# java-isaza-rivera-lawyers
# Endpoints (resumen)


- `POST /api/v1/leads` → crea un lead con: nombres, fecha de nacimiento, correo, teléfono, tipo de solicitud, consentimiento WA.
- `GET /api/v1/leads/{id}` → consulta el lead.
- `POST /api/v1/leads/{id}/confirm` → marca confirmación de cita (p. ej. tras coordinar por WhatsApp/Calendly).
- `POST /api/v1/payments/checkout` → genera link de pago (PSE/Nequi/Daviplata vía pasarela).
- `POST /webhooks/payments/{provider}` → recepción de webhooks de la pasarela.


---


# Ejemplos curl


```bash
# Crear lead
curl -X POST http://localhost:8080/api/v1/leads \
-H 'Content-Type: application/json' \
-d '{
"firstName":"Ana","lastName":"Perez",
"birthDate":"1995-04-10",
"email":"ana@example.com",
"phone":"3001234567",
"requestType":"DIVORCIO",
"whatsappConsent": true
}'


# Crear link de pago por $50.000 COP
curl -X POST http://localhost:8080/api/v1/payments/checkout \
-H 'Content-Type: application/json' \
-d '{"leadId":1, "amountInCents":5000000, "description":"Asesoría inicial"}'
```

---


# Notas de arquitectura y negocio

1) **Cuándo cobrar** (recomendación práctica):
- **Opción A (mi sugerida): anticipo/abono antes de la cita**: Genera link de pago al crear/confirmar el lead (mínimo $30k–$60k COP). Filtra no-shows y compromete al cliente. Si no asiste, se puede reprogramar o convertir a saldo a favor.
- **Opción B: pago contra-confirmación por WhatsApp**: tras coordinar fecha/hora, envías link de pago por WhatsApp/Email. Riesgo: algunos no pagan a tiempo.
- **Opción C: pago posterior**: mayor tasa de no pago/no-show. Menos recomendable.


2) **Pasarela en Colombia**: en vez de integrar PSE/Nequi/Daviplata directo, usa un **agregador** (p. ej., Wompi, ePayco, PlacetoPay) que te da links de cobro, soporta varios métodos, conciliación y **webhooks**. Cambias `StubPaymentService` por un `WompiPaymentService` y ya.


3) **WhatsApp Business**: usa la **Cloud API** (Meta). Flujo típico: plantilla de mensaje de confirmación + botón al link de pago/calendario. Implementa `WhatsappService` con HTTP (Bearer Token) y maneja rate limits.


4) **Privacidad**: almacena lo mínimo (cumple **Habeas Data** en CO). Guarda consentimiento para contacto por WA/Email. Añade políticas y endpoint para eliminación de datos si lo piden.


5) **Escalabilidad sencilla**: H2 para dev; luego Postgres/MySQL. Añade colas (SNS/SQS) para envío de mensajes y procesamiento de webhooks si crece el tráfico.


6) **Seguridad**: en producción agrega **Spring Security** con API keys o JWT, CORS restringido, validación de firmas de webhooks y **rate limiting**.


7) **Agenda**: puedes integrar Calendly/Google Calendar y disparar el link de pago al agendar.


> Con esto tienes un esqueleto listo para levantar, probar y extender a producción.