package com.isazariveralawyers.api.services;

import com.isazariveralawyers.api.services.WhatsappService;
import org.springframework.stereotype.Service;

@Service
public class StubWhatsappService implements WhatsappService {
    @Override
    public void sendConfirmationMessage(String phoneE164, String text) {
        // En producción: usar WhatsApp Business Cloud API
        System.out.println("[WHATSAPP] → " + phoneE164 + ": " + text);
    }
}
