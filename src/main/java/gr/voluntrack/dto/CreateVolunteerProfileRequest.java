package gr.voluntrack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateVolunteerProfileRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String phone;
}
