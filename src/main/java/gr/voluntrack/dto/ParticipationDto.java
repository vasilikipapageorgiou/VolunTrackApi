package gr.voluntrack.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipationDto {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private String eventStatus;
    private Long volunteerId;
    private String volunteerName;
    private String status;
    private LocalDateTime registeredAt;
}
