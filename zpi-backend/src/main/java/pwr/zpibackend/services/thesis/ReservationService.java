package pwr.zpibackend.services.thesis;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.ThesisOccupancyFullException;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.dto.thesis.ReservationDTO;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ThesisRepository thesisRepository;
    private final StudentRepository studentRepository;

    public Reservation addReservation(ReservationDTO reservation) {
        if (reservation.getThesisId() == null || reservation.getStudent() == null || reservation.getReservationDate() == null) {
            throw new IllegalArgumentException("Thesis, student and reservation date must be provided.");
        }
        if (reservationRepository.findByStudent_Mail(reservation.getStudent().getMail()) != null) {
            throw new AlreadyExistsException("Reservation for this student already exists.");
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
            throw new NotFoundException("Thesis with id " + reservation.getThesisId() + " does not exist.");
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
            throw new NotFoundException("Reservation with id " + id + " does not exist.");
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
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " does not exist."));
    }

    public Reservation deleteReservation(Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.deleteById(id);
                    thesisRepository.findById(reservation.getThesis().getId())
                            .ifPresent(thesis -> {
                                if (Objects.equals(thesis.getLeader().getId(), reservation.getStudent().getId())) {
                                    reservationRepository.findByThesis(thesis)
                                            .stream()
                                            .filter(res -> !Objects.equals(res.getId(), reservation.getId()))
                                            .findFirst()
                                            .ifPresentOrElse(res -> {
                                                thesis.setLeader(res.getStudent());
                                            }, () -> {
                                                thesis.setLeader(null);
                                            });
                                }
                                thesis.setOccupied(Math.min(thesis.getOccupied() - 1, 0));
                                if (thesis.getOccupied() == 0) {
                                    thesis.setLeader(null);
                                }
                                thesisRepository.save(thesis);
                            });
                    return reservation;
                })
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " does not exist."));
    }

    @Scheduled(cron = "0 0 3 * * ?")            // every day at 3:00 AM
    private void removeExpiredReservations() {
        LocalDateTime threshold = now().minusDays(1).minusHours(3); // 1 day and 3 hours ago
        reservationRepository.findAll().stream()
                .filter(reservation -> !reservation.isConfirmedByLeader() || !reservation.isConfirmedByStudent())
                .filter(reservation -> reservation.getReservationDate().isBefore(threshold))
                .forEach(reservation -> {
                    reservationRepository.delete(reservation);
                    thesisRepository.findById(reservation.getThesis().getId())
                            .ifPresent(thesis -> {
                                thesis.setOccupied(Math.min(thesis.getOccupied() - 1, 0));
                                if (thesis.getOccupied() == 0) {
                                    thesis.setLeader(null);
                                }
                                thesisRepository.save(thesis);
                            });
                });
    }
}
