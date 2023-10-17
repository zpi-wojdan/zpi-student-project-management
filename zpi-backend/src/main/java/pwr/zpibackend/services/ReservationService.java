package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.repositories.ReservationRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public Reservation addReservation(Reservation reservation) throws AlreadyExistsException {
        if (reservationRepository.findByStudent_Mail(reservation.getStudent().getMail()) != null) {
            throw new AlreadyExistsException();
        }
        Reservation newReservation = new Reservation();
        newReservation.setConfirmedByLeader(reservation.isConfirmedByLeader());
        newReservation.setConfirmedBySupervisor(reservation.isConfirmedBySupervisor());
        newReservation.setReadyForApproval(reservation.isReadyForApproval());
        newReservation.setReservationDate(reservation.getReservationDate());
        newReservation.setStudent(reservation.getStudent());
        newReservation.setThesis(reservation.getThesis());
        return reservationRepository.saveAndFlush(newReservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservation(Long id) throws NotFoundException {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            return reservation.get();
        } else {
            throw new NotFoundException();
        }
    }

    public Reservation updateReservation(Reservation newReservation, Long id) throws NotFoundException {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setConfirmedByLeader(newReservation.isConfirmedByLeader());
                    reservation.setConfirmedBySupervisor(newReservation.isConfirmedBySupervisor());
                    reservation.setReadyForApproval(newReservation.isReadyForApproval());
                    return reservationRepository.save(reservation);
                })
                .orElseThrow(NotFoundException::new);
    }

    public Reservation deleteReservation(Long id) throws NotFoundException {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.deleteById(id);
                    return reservation;
                })
                .orElseThrow(NotFoundException::new);
    }
}
