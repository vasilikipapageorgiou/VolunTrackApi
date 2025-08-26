package gr.voluntrack.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateEventRequest {
    private String title;
    private String description;
    private LocalDateTime eventDateTime;
    private String location;
}
