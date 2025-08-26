package gr.voluntrack.repository;

import gr.voluntrack.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}