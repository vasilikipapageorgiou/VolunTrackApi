package gr.voluntrack.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrganizationDto {
    private Long id;
    private String name;
    private String description;
    private String contactInfo;
    private Long userId;
    private String userEmail;
}
