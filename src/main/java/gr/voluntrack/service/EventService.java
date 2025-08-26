package gr.voluntrack.service;

import gr.voluntrack.dto.CreateEventRequest;
import gr.voluntrack.dto.EventDto;
import gr.voluntrack.model.Event;
import gr.voluntrack.model.EventStatus;
import gr.voluntrack.model.Organization;
import gr.voluntrack.repository.EventRepository;
import gr.voluntrack.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final OrganizationRepository organizationRepository;


    @Transactional
    public Event createEvent(Long organizationId, CreateEventRequest req) {
        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        Event e = Event.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .eventDateTime(req.getEventDateTime())
                .location(req.getLocation())
                .organization(org)
                .status(EventStatus.PENDING) // needs admin approval
                .build();

        return eventRepository.save(e);
    }

    @Transactional
    public Event updateEvent(Long organizationId, Long eventId, CreateEventRequest req) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        if (!event.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("You cannot update this event");
        }
        event.setTitle(req.getTitle());
        event.setDescription(req.getDescription());
        event.setEventDateTime(req.getEventDateTime());
        return eventRepository.save(event);
    }

    @Transactional
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
    }

    @Transactional
    public void cancelEvent(Long organizationId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        if (!event.getOrganization().getId().equals(organizationId)) {
            throw new SecurityException("You cannot update this event");
        }
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getApprovedEvents() {
        return eventRepository.findByStatus(EventStatus.APPROVED).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByOrganization(Long orgId) {
        return eventRepository.findByOrganizationId(orgId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventDto toDto(Event e) {
        return EventDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .eventDateTime(e.getEventDateTime())
                .location(e.getLocation())
                .status(e.getStatus().name())
                .organizationId(e.getOrganization().getId())
                .organizationName(e.getOrganization().getName())
                .build();
    }
}
