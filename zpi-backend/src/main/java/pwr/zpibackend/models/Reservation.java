package pwr.zpibackend.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(generator = "reservation_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "reservation_seq", sequenceName = "reservation_seq", allocationSize = 1)
    private long Id;
    @Column(name = "is_confirmed_by_leader")
    private boolean isConfirmedByLeader;
    @Column(name = "is_confirmed_by_supervisor")
    private boolean isConfirmedBySupervisor;
    @Column(name = "is_ready_for_approval")
    private boolean isReadyForApproval;
    @Column(name = "reservation_date", nullable = false)
    @NotNull(message = "Reservation date cannot be null")
    private LocalDate reservationDate;
    @JoinColumn(name = "mail", referencedColumnName = "mail", nullable = false)
    @OneToOne
    @NotNull(message = "Student cannot be null")
    private Student student;
    @JoinColumn(name = "thesis_id", referencedColumnName = "thesis_id", nullable = false)
    @ManyToOne
    @NotNull(message = "Thesis cannot be null")
    private Thesis thesis;
}
