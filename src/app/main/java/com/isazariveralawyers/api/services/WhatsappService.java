package com.isazariveralawyers.api.services;

public interface WhatsappService {
    void sendConfirmationMessage(String phoneE164, String text);
}
