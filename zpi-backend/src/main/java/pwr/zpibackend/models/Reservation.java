package pwr.zpibackend.models;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long Id;
    @Column(name = "is_confirmed_by_leader")
    private boolean isConfirmedByLeader;
    @Column(name = "is_confirmed_by_supervisor")
    private boolean isConfirmedBySupervisor;
    @Column(name = "is_ready_for_approval")
    private boolean isReadyForApproval;
    @Column(name = "reservation_date")
    private LocalDate reservationDate;

//    @JoinColumn(name = "mail", referencedColumnName = "mail")
//    private Student student;
    @JoinColumn(name = "thesis_id", referencedColumnName = "the_id")
    @ManyToOne
    private Thesis thesis;
}
