package gr.voluntrack.service;

import gr.voluntrack.dto.VolunteerProfileDto;
import gr.voluntrack.model.Event;
import gr.voluntrack.model.EventStatus;
import gr.voluntrack.model.VolunteerProfile;
import gr.voluntrack.repository.EventRepository;
import gr.voluntrack.repository.VolunteerProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final VolunteerProfileRepository volunteerProfileRepository;
    private final EventRepository eventRepository;

    public AdminService(VolunteerProfileRepository volunteerProfileRepository, EventRepository eventRepository) {
        this.volunteerProfileRepository = volunteerProfileRepository;
        this.eventRepository = eventRepository;
    }

    public List<VolunteerProfileDto> getAllVolunteersPendingApproval() {
        return volunteerProfileRepository.findByEnabledFalse().stream().map(
                        volunteer -> VolunteerProfileDto.builder()
                                .id(volunteer.getId())
                                .firstName(volunteer.getFirstName())
                                .lastName(volunteer.getLastName())
                                .phone(volunteer.getPhone())
                                .userId(volunteer.getUser().getId())
                                .userEmail(volunteer.getUser().getEmail())
                                .enabled(volunteer.isEnabled())
                                .build())
                .collect(Collectors.toList());
    }

    public void approveVolunteer(Long volunteerId) {
        Optional<VolunteerProfile> volunteerOpt = volunteerProfileRepository.findById(volunteerId);
        if (volunteerOpt.isPresent()) {
            VolunteerProfile volunteer = volunteerOpt.get();
            volunteer.setEnabled(true); // Ενεργοποιούμε τον εθελοντή
            volunteerProfileRepository.save(volunteer);
        } else {
            throw new IllegalArgumentException("Volunteer not found with id: " + volunteerId);
        }
    }

    public List<Event> getAllEventsPendingApproval() {
        // Επιστρέφει εκδηλώσεις με status PENDING
        return eventRepository.findByStatus(EventStatus.PENDING);
    }

    public void approveEvent(Long eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            event.setStatus(EventStatus.APPROVED); // Αλλάζουμε το status σε APPROVED
            eventRepository.save(event);
        } else {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }
    }
}
