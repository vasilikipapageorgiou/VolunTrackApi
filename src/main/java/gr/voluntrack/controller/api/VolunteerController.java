package gr.voluntrack.controller.api;

import gr.voluntrack.dto.CreateVolunteerProfileRequest;
import gr.voluntrack.dto.EventDto;
import gr.voluntrack.dto.ParticipationDto;
import gr.voluntrack.dto.VolunteerProfileDto;
import gr.voluntrack.model.Participation;
import gr.voluntrack.model.User;
import gr.voluntrack.repository.UserRepository;
import gr.voluntrack.repository.VolunteerProfileRepository;
import gr.voluntrack.service.EventService;
import gr.voluntrack.service.ParticipationService;
import gr.voluntrack.service.VolunteerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer")
@RequiredArgsConstructor
public class VolunteerController {

    private final EventService eventService;
    private final ParticipationService participationService;
    private final UserRepository userRepository;
    private final VolunteerProfileRepository volunteerProfileRepository;
    private final VolunteerProfileService volunteerProfileService;

    @PostMapping("/profile")
    public ResponseEntity<VolunteerProfileDto> createProfile(@Valid @RequestBody CreateVolunteerProfileRequest req,
                                                             Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        VolunteerProfileDto dto = volunteerProfileService.createProfileForUser(user.getId(), req);
        return ResponseEntity.ok(dto);
    }

    // Get current user's profile
    @GetMapping("/profile")
    public ResponseEntity<VolunteerProfileDto> getMyProfile(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        VolunteerProfileDto dto = volunteerProfileService.getProfileByUserId(user.getId());
        return ResponseEntity.ok(dto);
    }

    // Update current user's profile
    @PutMapping("/profile")
    public ResponseEntity<VolunteerProfileDto> updateProfile(@Valid @RequestBody CreateVolunteerProfileRequest req,
                                                             Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        VolunteerProfileDto dto = volunteerProfileService.updateProfile(user.getId(), req);
        return ResponseEntity.ok(dto);
    }

    // list approved events
    @GetMapping("/events")
    public ResponseEntity<List<EventDto>> listEvents() {
        return ResponseEntity.ok(eventService.getApprovedEvents());
    }

    // register for event
    @PostMapping("/events/{eventId}/register")
    public ResponseEntity<ParticipationDto> register(@PathVariable Long eventId, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long volunteerId = volunteerProfileRepository.findByUserId(user.getId()).orElseThrow().getId();
        Participation p = participationService.registerVolunteer(eventId, volunteerId);
        return ResponseEntity.ok(participationService.getParticipationsByVolunteer(volunteerId)
                .stream().filter(d -> d.getEventId().equals(eventId)).findFirst().orElse(null));
    }

    // volunteer's participations
    @GetMapping("/my/participations")
    public ResponseEntity<List<ParticipationDto>> myParticipations(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long volunteerId = volunteerProfileRepository.findByUserId(user.getId()).orElseThrow().getId();
        return ResponseEntity.ok(participationService.getParticipationsByVolunteer(volunteerId));
    }

    // check-in (volunteer)
    @PostMapping("/participations/{id}/checkin")
    public ResponseEntity<Void> checkIn(@PathVariable Long id, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long volunteerId = volunteerProfileRepository.findByUserId(user.getId()).orElseThrow().getId();
        participationService.checkIn(id, volunteerId);
        return ResponseEntity.ok().build();
    }
}
