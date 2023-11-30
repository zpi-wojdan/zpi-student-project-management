package pwr.zpibackend.models.thesis;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private long Id;
    @Column(name = "is_confirmed_by_leader")
    private boolean isConfirmedByLeader;
    @Column(name = "is_confirmed_by_supervisor")
    private boolean isConfirmedBySupervisor;
    @Column(name = "is_confirmed_by_student")
    private boolean isConfirmedByStudent;
    @Column(name = "is_ready_for_approval")
    private boolean isReadyForApproval;
    @Column(name = "reservation_date", nullable = false)
    @NotNull(message = "Reservation date cannot be null")
    private LocalDateTime reservationDate;
    @Column(name = "sent_for_approval_date")
    @NotNull(message = "Reservation date cannot be null")
    private LocalDateTime sentForApprovalDate;
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    @OneToOne
//    @NotNull(message = "Student cannot be null")
    private Student student;
    @JoinColumn(name = "thesis_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
//    @NotNull(message = "Thesis cannot be null")
    @JsonIgnore
    private Thesis thesis;
}
