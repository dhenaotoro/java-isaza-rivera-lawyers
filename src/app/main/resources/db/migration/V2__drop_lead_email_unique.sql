-- Flyway V2: allow multiple leads with the same email
ALTER TABLE `lead`
  DROP INDEX `uk_lead_email`;
