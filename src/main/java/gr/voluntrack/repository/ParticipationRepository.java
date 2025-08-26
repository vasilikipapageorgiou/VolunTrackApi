package gr.voluntrack.repository;

import gr.voluntrack.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByVolunteerId(Long volunteerId);
    List<Participation> findByEventId(Long eventId);
    Optional<Participation> findByEventIdAndVolunteerId(Long eventId, Long volunteerId);
    List<Participation> findByEventOrganizationId(Long orgId);
}
