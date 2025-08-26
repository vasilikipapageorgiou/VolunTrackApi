package gr.voluntrack.service;

import gr.voluntrack.dto.CreateVolunteerProfileRequest;
import gr.voluntrack.dto.VolunteerProfileDto;
import gr.voluntrack.model.User;
import gr.voluntrack.model.VolunteerProfile;
import gr.voluntrack.repository.UserRepository;
import gr.voluntrack.repository.VolunteerProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VolunteerProfileService {

    private final VolunteerProfileRepository volunteerProfileRepository;
    private final UserRepository userRepository;

    public VolunteerProfileService(VolunteerProfileRepository volunteerProfileRepository,
                                   UserRepository userRepository) {
        this.volunteerProfileRepository = volunteerProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public VolunteerProfileDto createProfileForUser(Long userId, CreateVolunteerProfileRequest req) {
        if (volunteerProfileRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Volunteer profile already exists for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        VolunteerProfile profile = VolunteerProfile.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .phone(req.getPhone())
                .enabled(false)
                .user(user)
                .build();

        VolunteerProfile saved = volunteerProfileRepository.save(profile);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public VolunteerProfileDto getProfileByUserId(Long userId) {
        VolunteerProfile p = volunteerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Volunteer profile not found"));
        return toDto(p);
    }

    @Transactional
    public VolunteerProfileDto updateProfile(Long userId, CreateVolunteerProfileRequest req) {
        VolunteerProfile p = volunteerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Volunteer profile not found"));

        p.setFirstName(req.getFirstName());
        p.setLastName(req.getLastName());
        p.setPhone(req.getPhone());

        return toDto(volunteerProfileRepository.save(p));
    }

    private VolunteerProfileDto toDto(VolunteerProfile p) {
        return VolunteerProfileDto.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .phone(p.getPhone())
                .userId(p.getUser().getId())
                .userEmail(p.getUser().getEmail())
                .enabled(p.isEnabled())
                .build();
    }
}
