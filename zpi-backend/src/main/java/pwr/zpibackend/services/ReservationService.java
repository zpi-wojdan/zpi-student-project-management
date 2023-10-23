package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.ThesisOccupancyFullException;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.repositories.ReservationRepository;
import pwr.zpibackend.repositories.ThesisRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ThesisRepository thesisRepository;

    public Reservation addReservation(Reservation reservation) throws AlreadyExistsException, ThesisOccupancyFullException {
        if (reservation.getThesis() == null || reservation.getStudent() == null || reservation.getReservationDate() == null) {
            throw new IllegalArgumentException();
        }
        if (reservationRepository.findByStudent_Mail(reservation.getStudent().getMail()) != null) {
            throw new AlreadyExistsException();
        }
        Optional<Thesis> thesisOptional = thesisRepository.findById(reservation.getThesis().getId());
        if (thesisOptional.isPresent()) {
            Thesis thesis = thesisOptional.get();
            if (Objects.equals(thesis.getOccupied(), thesis.getNum_people())) {
                throw new ThesisOccupancyFullException();
            } else if (thesis.getOccupied() == 0) {
                thesis.setLeader(reservation.getStudent());
            } else {
                thesis.setOccupied(thesis.getOccupied() + 1);
                thesisRepository.saveAndFlush(thesis);
            }
        } else {
            throw new IllegalArgumentException();
        }

        Reservation newReservation = new Reservation();
        newReservation.setConfirmedByLeader(reservation.isConfirmedByLeader());
        newReservation.setConfirmedBySupervisor(reservation.isConfirmedBySupervisor());
        newReservation.setReadyForApproval(reservation.isReadyForApproval());
        newReservation.setReservationDate(reservation.getReservationDate());
        newReservation.setStudent(reservation.getStudent());
        newReservation.setThesis(reservation.getThesis());
        reservationRepository.saveAndFlush(newReservation);
        return newReservation;
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
                    reservation.setConfirmedByStudent(newReservation.isConfirmedByStudent());
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
