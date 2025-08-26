package gr.voluntrack.service;

import gr.voluntrack.dto.ParticipationDto;
import gr.voluntrack.model.Event;
import gr.voluntrack.model.Participation;
import gr.voluntrack.model.ParticipationStatus;
import gr.voluntrack.model.VolunteerProfile;
import gr.voluntrack.repository.EventRepository;
import gr.voluntrack.repository.ParticipationRepository;
import gr.voluntrack.repository.VolunteerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;

    @Transactional
    public Participation registerVolunteer(Long eventId, Long volunteerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        VolunteerProfile volunteer = volunteerProfileRepository.findById(volunteerId)
                .orElseThrow(() -> new IllegalArgumentException("Volunteer not found"));

        // ensure event is approved
        if (event.getStatus() != null && !event.getStatus().name().equals("APPROVED")) {
            throw new IllegalStateException("Event is not open for registration");
        }

        // check if already registered
        if (participationRepository.findByEventIdAndVolunteerId(eventId, volunteerId).isPresent()) {
            throw new IllegalStateException("Already registered");
        }

        Participation p = Participation.builder()
                .event(event)
                .volunteer(volunteer)
                .status(ParticipationStatus.PENDING)
                .registeredAt(LocalDateTime.now())
                .build();

        return participationRepository.save(p);
    }

    @Transactional
    public Participation approveParticipation(Long participationId, Long orgId) {
        Participation p = participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participation not found"));

        // only organization that owns event can approve
        if (!p.getEvent().getOrganization().getId().equals(orgId)) {
            throw new SecurityException("Not authorized to approve this participation");
        }

        p.setStatus(ParticipationStatus.APPROVED);
        return participationRepository.save(p);
    }

    @Transactional
    public Participation checkIn(Long participationId, Long volunteerId) {
        Participation p = participationRepository.findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("Participation not found"));

        if (!p.getVolunteer().getId().equals(volunteerId)) {
            throw new SecurityException("Not authorized to check-in");
        }

        p.setStatus(ParticipationStatus.CHECKED_IN);
        return participationRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<ParticipationDto> getParticipationsByVolunteer(Long volunteerId) {
        return participationRepository.findByVolunteerId(volunteerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipationDto> getParticipationsByOrganization(Long orgId) {
        return participationRepository.findByEventOrganizationId(orgId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipationDto> getRegistrationsForEvent(Long eventId, Long orgId) {
        // only org that owns event may view
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        if (!event.getOrganization().getId().equals(orgId)) {
            throw new SecurityException("Not authorized to view registrations");
        }

        return participationRepository.findByEventId(eventId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ParticipationDto toDto(Participation p) {
        return ParticipationDto.builder()
                .id(p.getId())
                .eventId(p.getEvent().getId())
                .eventTitle(p.getEvent().getTitle())
                .eventStatus(p.getEvent().getStatus().name())
                .volunteerId(p.getVolunteer().getId())
                .volunteerName(p.getVolunteer().getFirstName() + " " + p.getVolunteer().getLastName())
                .status(p.getStatus().name())
                .registeredAt(p.getRegisteredAt())
                .build();
    }
}
