package gr.voluntrack.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrganizationRequest {
    @NotBlank
    private String name;
    private String description;
    private String contactInfo;
}
