package pwr.zpibackend.services.thesis;

import pwr.zpibackend.dto.thesis.ReservationDTO;
import pwr.zpibackend.models.thesis.Reservation;

import java.util.List;

public interface IReservationService {
    List<Reservation> getAllReservations();
    Reservation getReservation(Long id);
    Reservation addReservation(ReservationDTO reservation);
    Reservation updateReservation(Reservation newReservation, Long id);
    Reservation deleteReservation(Long id);
    void removeExpiredReservations();
    void acceptReservationsMadeBySupervisor();
}
