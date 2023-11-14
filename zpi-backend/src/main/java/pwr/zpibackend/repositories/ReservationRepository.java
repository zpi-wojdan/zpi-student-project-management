package pwr.zpibackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.models.Student;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Reservation findByStudent_Mail(String mail);
}
