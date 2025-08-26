package gr.voluntrack.controller.api;

import gr.voluntrack.dto.*;
import gr.voluntrack.model.Participation;
import gr.voluntrack.model.Role;
import gr.voluntrack.model.User;
import gr.voluntrack.repository.OrganizationRepository;
import gr.voluntrack.repository.UserRepository;
import gr.voluntrack.service.EventService;
import gr.voluntrack.service.OrganizationService;
import gr.voluntrack.service.ParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final EventService eventService;
    private final ParticipationService participationService;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationService organizationService;

    @PostMapping("/profile")
    public ResponseEntity<OrganizationDto> createOrganization(@Valid @RequestBody CreateOrganizationRequest req,
                                                              Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        OrganizationDto dto = organizationService.createOrganizationForUser(user.getId(), req);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/profile")
    public ResponseEntity<OrganizationDto> getMyOrganization(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        OrganizationDto dto = organizationService.getOrganizationByUserId(user.getId());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    public ResponseEntity<OrganizationDto> updateOrganization(@Valid @RequestBody CreateOrganizationRequest req,
                                                              Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        OrganizationDto dto = organizationService.updateOrganization(user.getId(), req);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/events")
    public ResponseEntity<EventDto> createEvent(@RequestBody CreateEventRequest req, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long orgId = organizationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Organization profile missing")).getId();

        EventDto dto = eventService.toDto(eventService.createEvent(orgId, req));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/events/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long eventId, @RequestBody CreateEventRequest req, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long orgId = organizationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Organization profile missing")).getId();

        EventDto dto = eventService.toDto(eventService.updateEvent(orgId, eventId, req));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long eventId) {
        EventDto dto = eventService.toDto(eventService.getEvent(eventId));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/events/{eventId}/cancel")
    public ResponseEntity<Void> cancelEvent(@PathVariable Long eventId, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long orgId = organizationRepository.findByUserId(user.getId()).orElseThrow().getId();
        eventService.cancelEvent(orgId, eventId);
        return ResponseEntity.ok().build();
    }


    // list org's events
    @GetMapping("/events")
    public ResponseEntity<List<EventDto>> getMyEvents(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long orgId = organizationRepository.findByUserId(user.getId()).orElseThrow(() -> new IllegalStateException("Organization profile missing")).getId();
        return ResponseEntity.ok(eventService.getEventsByOrganization(orgId));
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<ParticipationDto>> getRegistrations(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long orgId = organizationRepository.findByUserId(user.getId()).orElseThrow().getId();
        return ResponseEntity.ok(participationService.getParticipationsByOrganization(orgId));
    }

    // view registrations for an event
    @GetMapping("/events/{eventId}/registrations")
    public ResponseEntity<List<ParticipationDto>> getEventRegistrations(@PathVariable Long eventId, Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long orgId = organizationRepository.findByUserId(user.getId()).orElseThrow().getId();
        return ResponseEntity.ok(participationService.getRegistrationsForEvent(eventId, orgId));
    }

    @PostMapping("/events/{eventId}/registrations/{participationId}/approve")
    public ResponseEntity<Void> approveParticipation(@PathVariable Long eventId,
                                                     @PathVariable Long participationId,
                                                     Authentication auth) {
        User user = userRepository.findByEmail(auth.getName()).orElseThrow();
        Long orgId = organizationRepository.findByUserId(user.getId()).orElseThrow().getId();
        participationService.approveParticipation(participationId, orgId);
        return ResponseEntity.ok().build();
    }
}
