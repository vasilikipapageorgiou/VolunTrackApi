package gr.voluntrack.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VolunteerProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private Long userId;
    private String userEmail;
    private boolean enabled;
}
