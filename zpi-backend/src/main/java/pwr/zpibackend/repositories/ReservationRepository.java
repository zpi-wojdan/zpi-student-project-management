package pwr.zpibackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pwr.zpibackend.models.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
