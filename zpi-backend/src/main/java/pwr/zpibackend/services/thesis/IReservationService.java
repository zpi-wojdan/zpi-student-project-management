package pwr.zpibackend.services.thesis;

import pwr.zpibackend.dto.thesis.ReservationDTO;
import pwr.zpibackend.models.thesis.Reservation;

import java.util.List;

public interface IReservationService {
    List<Reservation> getAllReservations();
    Reservation getReservation(Long id);
    Reservation addReservation(ReservationDTO reservation);
    List<Reservation> addListReservation(List<ReservationDTO> reservations);
    Reservation updateReservation(Reservation newReservation, Long id);
    List<Reservation> updateReservationsForThesis(Long thesisId, List<Reservation> newReservations);
    Reservation deleteReservation(Long id);
    void removeExpiredReservations();
    void acceptReservationsMadeBySupervisor();
}
