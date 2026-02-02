package com.isazariveralawyers.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.isazariveralawyers.api.models.Lead;
import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long> {
    Optional<Lead> findByEmail(String email);
}
