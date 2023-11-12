package pwr.zpibackend.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.ThesisOccupancyFullException;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.dto.ReservationDTO;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.repositories.ReservationRepository;
import pwr.zpibackend.repositories.StudentRepository;
import pwr.zpibackend.repositories.ThesisRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ThesisRepository thesisRepository;
    private final StudentRepository studentRepository;

    public Reservation addReservation(ReservationDTO reservation) {
        if (reservation.getThesisId() == null || reservation.getStudent() == null || reservation.getReservationDate() == null) {
            throw new IllegalArgumentException();
        }
        if (reservationRepository.findByStudent_Mail(reservation.getStudent().getMail()) != null) {
            throw new AlreadyExistsException();
        }

        Reservation newReservation = new Reservation();
        newReservation.setConfirmedByLeader(reservation.isConfirmedByLeader());
        newReservation.setConfirmedBySupervisor(reservation.isConfirmedBySupervisor());
        newReservation.setConfirmedByStudent(reservation.isConfirmedByStudent());
        newReservation.setReadyForApproval(reservation.isReadyForApproval());
        newReservation.setReservationDate(reservation.getReservationDate());
        newReservation.setSentForApprovalDate(reservation.getSentForApprovalDate());
        Student student = studentRepository.findById(reservation.getStudent().getId()).get();
        newReservation.setStudent(student);

        Optional<Thesis> thesisOptional = thesisRepository.findById(reservation.getThesisId());
        if (thesisOptional.isPresent()) {
            Thesis thesis = thesisOptional.get();
            if (Objects.equals(thesis.getOccupied(), thesis.getNumPeople())) {
                throw new ThesisOccupancyFullException();
            } else {
                if (thesis.getOccupied() == 0) {
                    thesis.setLeader(student);
                    newReservation.setConfirmedByStudent(true);
                }
                List<Reservation> reservations = thesis.getReservations();
                reservations.add(newReservation);
                thesis.setReservations(reservations);
                thesis.setOccupied(thesis.getOccupied() + 1);
                newReservation.setThesis(thesis);
            }
        } else {
            throw new IllegalArgumentException();
        }
        reservationRepository.saveAndFlush(newReservation);
        return newReservation;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservation(Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            return reservation.get();
        } else {
            throw new NotFoundException();
        }
    }

    public Reservation updateReservation(Reservation newReservation, Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setConfirmedByLeader(newReservation.isConfirmedByLeader());
                    reservation.setConfirmedBySupervisor(newReservation.isConfirmedBySupervisor());
                    reservation.setReadyForApproval(newReservation.isReadyForApproval());
                    reservation.setConfirmedByStudent(newReservation.isConfirmedByStudent());
                    reservation.setSentForApprovalDate(newReservation.getSentForApprovalDate());
                    return reservationRepository.save(reservation);
                })
                .orElseThrow(NotFoundException::new);
    }

    public Reservation deleteReservation(Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.deleteById(id);
                    thesisRepository.findById(reservation.getThesis().getId())
                            .ifPresent(thesis -> {
                                thesis.setOccupied(thesis.getOccupied() - 1);
                                if (thesis.getOccupied() == 0) {
                                    thesis.setLeader(null);
                                }
                                thesisRepository.save(thesis);
                            });
                    return reservation;
                })
                .orElseThrow(NotFoundException::new);
    }
}
