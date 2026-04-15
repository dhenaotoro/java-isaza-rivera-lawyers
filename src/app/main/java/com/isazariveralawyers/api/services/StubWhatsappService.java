package com.isazariveralawyers.api.services;

import org.springframework.stereotype.Service;

@Service
public class StubWhatsappService implements WhatsappService {
    @Override
    public void sendConfirmationMessage(String phoneE164, String text) {
        // En producción: usar WhatsApp Business Cloud API
        System.out.println("[WHATSAPP] → " + phoneE164 + ": " + text);
    }

    @Override
    public void sendDocumentMessage(String phoneE164, String fileName, byte[] content, String caption) {
        System.out.println("[WHATSAPP-DOC] → " + phoneE164 + " file=" + fileName + " bytes=" + content.length + " caption=" + caption);
    }
}
