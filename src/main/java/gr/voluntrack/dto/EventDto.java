package gr.voluntrack.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDateTime;
    private String location;
    private String status;
    private Long organizationId;
    private String organizationName;
}
