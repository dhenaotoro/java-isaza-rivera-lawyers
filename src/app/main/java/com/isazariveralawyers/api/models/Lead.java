package com.isazariveralawyers.api.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Lead {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String city;

    private String phoneE164;

    private String summary;

    private String source;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    private LeadStatus status = LeadStatus.NEW;

    private boolean hasMinors;
    private boolean dataProcessingConsent;
    private boolean whatsappConsent;
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
