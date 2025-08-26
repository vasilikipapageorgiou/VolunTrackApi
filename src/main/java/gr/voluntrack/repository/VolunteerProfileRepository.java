package gr.voluntrack.repository;

import gr.voluntrack.model.VolunteerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VolunteerProfileRepository extends JpaRepository<VolunteerProfile, Long> {
    List<VolunteerProfile> findByEnabledFalse();
    Optional<VolunteerProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}