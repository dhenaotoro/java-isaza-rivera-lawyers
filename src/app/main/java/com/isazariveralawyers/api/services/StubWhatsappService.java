package com.isazariveralawyers.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StubWhatsappService implements WhatsappService {
    private static final Logger log = LoggerFactory.getLogger(StubWhatsappService.class);

    @Override
    public void sendConfirmationMessage(String phoneE164, String text) {
        log.info("WhatsApp stub confirmation message. recipient='{}', text='{}'", phoneE164, text);
    }

    @Override
    public void sendDocumentMessage(String phoneE164, String fileName, byte[] content, String caption) {
        log.info(
            "WhatsApp stub document message. recipient='{}', fileName='{}', bytes={}, caption='{}'",
            phoneE164,
            fileName,
            content.length,
            caption
        );
    }
}
