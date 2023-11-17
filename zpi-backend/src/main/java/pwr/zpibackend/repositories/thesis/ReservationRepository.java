package pwr.zpibackend.repositories.thesis;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.thesis.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Reservation findByStudent_Mail(String mail);
}
