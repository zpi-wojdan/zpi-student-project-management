package pwr.zpibackend.dto.thesis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pwr.zpibackend.models.user.Student;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private boolean isConfirmedByLeader;
    private boolean isConfirmedBySupervisor;
    private boolean isConfirmedByStudent;
    private boolean isReadyForApproval;
    private LocalDateTime reservationDate;
    private LocalDateTime sentForApprovalDate;
    private Student student;
    private Long thesisId;
}
