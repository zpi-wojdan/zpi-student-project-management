package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.repositories.ReservationRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Reservation addReservation(Reservation reservation) {
        return reservationRepository.saveAndFlush(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservation(Long id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public Reservation updateReservation(Reservation newReservation, Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setConfirmedByLeader(newReservation.isConfirmedByLeader());
                    reservation.setConfirmedBySupervisor(newReservation.isConfirmedBySupervisor());
                    reservation.setReadyForApproval(newReservation.isReadyForApproval());
                    reservation.setReservationDate(newReservation.getReservationDate());
//                    reservation.setStudent(newReservation.getStudent());
                    reservation.setThesis(newReservation.getThesis());
                    return reservationRepository.save(reservation);
                })
                .orElseGet(() -> {
                    newReservation.setId(id);
                    return reservationRepository.save(newReservation);
                });
    }

    public Reservation deleteReservation(Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.deleteById(id);
                    return reservation;
                })
                .orElse(null);
    }
}
