-- Flyway V1: create lead table matching com.isazariveralawyers.api.models.Lead
CREATE TABLE IF NOT EXISTS `lead` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(255),
  `last_name` VARCHAR(255),
  `email` VARCHAR(255),
  `city` VARCHAR(255),
  `phone_e164` VARCHAR(255),
  `summary` TEXT,
  `source` VARCHAR(255),
  `request_type` VARCHAR(255),
  `status` VARCHAR(255) NOT NULL DEFAULT 'NEW',
  `has_minors` BOOLEAN NOT NULL DEFAULT FALSE,
  `data_processing_consent` BOOLEAN NOT NULL DEFAULT FALSE,
  `whatsapp_consent` BOOLEAN NOT NULL DEFAULT FALSE,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lead_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
