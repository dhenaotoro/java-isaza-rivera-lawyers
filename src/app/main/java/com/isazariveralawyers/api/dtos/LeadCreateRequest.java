package com.isazariveralawyers.api.dtos;

import com.isazariveralawyers.api.models.RequestType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class LeadCreateRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;


    @Past
    private LocalDate birthDate;


    @Email @NotBlank
    private String email;


    @NotBlank
    private String phone; // can be in a colombian format; it gets normalized to E.164


    @NotNull
    private RequestType requestType;


    private Boolean whatsappConsent = Boolean.TRUE;


    // getters/setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) { this.requestType = requestType; }
    public Boolean getWhatsappConsent() { return whatsappConsent; }
    public void setWhatsappConsent(Boolean whatsappConsent) { this.whatsappConsent = whatsappConsent; }
}
