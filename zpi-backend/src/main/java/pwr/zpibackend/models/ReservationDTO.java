package pwr.zpibackend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private boolean isConfirmedByLeader;
    private boolean isConfirmedBySupervisor;
    private boolean isConfirmedByStudent;
    private boolean isReadyForApproval;
    private LocalDate reservationDate;
    private Student student;
    private Long thesisId;
}
