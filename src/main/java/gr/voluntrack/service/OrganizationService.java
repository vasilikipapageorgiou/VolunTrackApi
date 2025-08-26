package gr.voluntrack.service;

import gr.voluntrack.dto.CreateOrganizationRequest;
import gr.voluntrack.dto.OrganizationDto;
import gr.voluntrack.model.Organization;
import gr.voluntrack.model.User;
import gr.voluntrack.repository.OrganizationRepository;
import gr.voluntrack.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public OrganizationService(OrganizationRepository organizationRepository,
                               UserRepository userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrganizationDto createOrganizationForUser(Long userId, CreateOrganizationRequest req) {
        if (organizationRepository.existsByUserId(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Organization profile already exists for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Organization org = Organization.builder()
                .name(req.getName())
                .description(req.getDescription())
                .contactInfo(req.getContactInfo())
                .user(user)
                .build();

        Organization saved = organizationRepository.save(org);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public OrganizationDto getOrganizationByUserId(Long userId) {
        Organization o = organizationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found"));
        return toDto(o);
    }

    @Transactional
    public OrganizationDto updateOrganization(Long userId, CreateOrganizationRequest req) {
        Organization o = organizationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found"));

        o.setName(req.getName());
        o.setDescription(req.getDescription());
        o.setContactInfo(req.getContactInfo());

        return toDto(organizationRepository.save(o));
    }

    private OrganizationDto toDto(Organization o) {
        return OrganizationDto.builder()
                .id(o.getId())
                .name(o.getName())
                .description(o.getDescription())
                .contactInfo(o.getContactInfo())
                .userId(o.getUser().getId())
                .userEmail(o.getUser().getEmail())
                .build();
    }
}
