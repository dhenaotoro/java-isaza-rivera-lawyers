package com.isazariveralawyers.api.dtos;

import com.isazariveralawyers.api.models.LeadStatus;
import com.isazariveralawyers.api.models.RequestType;
import java.time.LocalDate;

public class LeadCreateResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneE164;
    private RequestType requestType;
    private LeadStatus status;


    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneE164() { return phoneE164; }
    public void setPhoneE164(String phoneE164) { this.phoneE164 = phoneE164; }
    public RequestType getRequestType() { return requestType; }
    public void setRequestType(RequestType requestType) { this.requestType = requestType; }
    public LeadStatus getStatus() { return status; }
    public void setStatus(LeadStatus status) { this.status = status; }
}
