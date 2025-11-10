import org.springframework.data.jpa.repository.JpaRepository;

import com.isazariveralawyers.api.entities.Lead;
import java.util.Optional;


public interface LeadRepository extends JpaRepository<Lead, Long> {
    Optional<Lead> findByEmail(String email);
}
