package gr.voluntrack.repository;

import gr.voluntrack.model.Event;
import gr.voluntrack.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByOrganizationId(Long organizationId);
}