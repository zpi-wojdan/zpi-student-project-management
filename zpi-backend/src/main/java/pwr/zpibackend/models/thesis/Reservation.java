package pwr.zpibackend.models.thesis;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pwr.zpibackend.models.user.Student;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the reservation.", example = "1")
    private long id;

    @Column(name = "is_confirmed_by_leader")
    @Schema(description = "Flag indicating whether the reservation has been confirmed by the leader.", example = "true")
    private boolean isConfirmedByLeader;

    @Column(name = "is_confirmed_by_supervisor")
    @Schema(description = "Flag indicating whether the reservation has been confirmed by the supervisor.",
            example = "true")
    private boolean isConfirmedBySupervisor;

    @Column(name = "is_confirmed_by_student")
    @Schema(description = "Flag indicating whether the reservation has been confirmed by the student.",
            example = "true")
    private boolean isConfirmedByStudent;

    @Column(name = "is_ready_for_approval")
    @Schema(description = "Flag indicating whether the reservation is ready for approval.", example = "true")
    private boolean isReadyForApproval;

    @Column(name = "reservation_date", nullable = false)
    @NotNull(message = "Reservation date cannot be null")
    @Schema(description = "Date of the reservation.")
    private LocalDateTime reservationDate;

    @Column(name = "sent_for_approval_date")
    @NotNull(message = "Reservation date cannot be null")
    @Schema(description = "Date when the reservation was sent for approval.")
    private LocalDateTime sentForApprovalDate;

    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    @OneToOne
    @NotNull(message = "Student cannot be null")
    @Schema(description = "Student who made the reservation.")
    private Student student;

    @JoinColumn(name = "thesis_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    @NotNull(message = "Thesis cannot be null")
    @JsonIgnore
    @ToString.Exclude
    private Thesis thesis;
}
