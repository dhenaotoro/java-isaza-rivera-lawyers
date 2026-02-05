package com.isazariveralawyers.api.dtos;

import com.isazariveralawyers.api.models.RequestType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Data;

@Data
public class LeadCreateRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String city;
    @Email @NotBlank
    private String email;
    @NotBlank
    private String phone; // puede estar en formato colombiano; se normaliza a E.164
    @NotBlank
    private String summary;
    @NotNull
    private RequestType requestType;
    private boolean hasMinors = false;
    private boolean dataProcessingConsent = Boolean.TRUE;
    private boolean whatsappConsent = Boolean.TRUE;
    private String source = "instagram";
}
