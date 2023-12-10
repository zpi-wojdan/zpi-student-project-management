package pwr.zpibackend.dto.thesis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.models.user.Student;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    @Schema(description = "Flag indicating whether the reservation has been confirmed by the leader.", example = "true")
    private boolean isConfirmedByLeader;

    @Schema(description = "Flag indicating whether the reservation has been confirmed by the supervisor.",
            example = "true")
    private boolean isConfirmedBySupervisor;

    @Schema(description = "Flag indicating whether the reservation has been confirmed by the student.",
            example = "true")
    private boolean isConfirmedByStudent;

    @Schema(description = "Flag indicating whether the reservation is ready for approval.", example = "true")
    private boolean isReadyForApproval;

    @Schema(description = "Date of the reservation.", example = "2024-01-01 12:00:00")
    private LocalDateTime reservationDate;

    @Schema(description = "Date when the reservation was sent for approval.", example = "2024-01-01 12:00:00")
    private LocalDateTime sentForApprovalDate;

    @Schema(description = "Student who made the reservation.")
    private Student student;

    @Schema(description = "Thesis for which the reservation was made.", example = "1")
    private Long thesisId;
}
