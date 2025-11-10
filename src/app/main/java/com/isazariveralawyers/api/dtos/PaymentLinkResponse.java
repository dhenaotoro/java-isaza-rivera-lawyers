package com.isazariveralawyers.api.dtos;

public class PaymentLinkResponse {
    private String paymentLinkUrl;
    private String providerReference;
    
    
    public PaymentLinkResponse() {}
    public PaymentLinkResponse(String url, String ref) { this.paymentLinkUrl = url; this.providerReference = ref; }
    
    
    public String getPaymentLinkUrl() { return paymentLinkUrl; }
    public void setPaymentLinkUrl(String paymentLinkUrl) { this.paymentLinkUrl = paymentLinkUrl; }
    public String getProviderReference() { return providerReference; }
    public void setProviderReference(String providerReference) { this.providerReference = providerReference; }
}