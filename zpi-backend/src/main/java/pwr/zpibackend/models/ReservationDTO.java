package pwr.zpibackend.models;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationDTO {
    private boolean isConfirmedByLeader;
    private boolean isConfirmedBySupervisor;
    private boolean isConfirmedByStudent;
    private boolean isReadyForApproval;
    private LocalDate reservationDate;
    private Student student;
    private Long thesisId;
}
