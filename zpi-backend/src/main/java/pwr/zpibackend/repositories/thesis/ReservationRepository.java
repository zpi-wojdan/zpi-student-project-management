package pwr.zpibackend.repositories.thesis;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.models.thesis.Thesis;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Reservation findByStudent_Mail(String mail);
    Reservation findByStudentId(Long id);
    Optional<Reservation> findByThesis(Thesis thesis);
}
