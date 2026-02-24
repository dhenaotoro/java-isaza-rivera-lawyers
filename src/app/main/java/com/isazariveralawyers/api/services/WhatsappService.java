package com.isazariveralawyers.api.services;

public interface WhatsappService {
    void sendConfirmationMessage(String phoneE164, String text);

    void sendDocumentMessage(String phoneE164, String fileName, byte[] content, String caption);
}
