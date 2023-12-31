package pwr.zpibackend.services.impl.thesis;

import lombok.AllArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.ThesisOccupancyFullException;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.dto.thesis.ReservationDTO;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.services.mailing.IMailService;
import pwr.zpibackend.services.thesis.IReservationService;
import pwr.zpibackend.utils.MailTemplates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
@AllArgsConstructor
public class ReservationService implements IReservationService {
    private final ReservationRepository reservationRepository;
    private final ThesisRepository thesisRepository;
    private final StudentRepository studentRepository;
    private final IMailService mailService;
    private final StatusRepository statusRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Reservation addReservation(ReservationDTO reservation) {
        if (reservation.getThesisId() == null || reservation.getStudent() == null
                || reservation.getReservationDate() == null) {
            throw new IllegalArgumentException("Thesis, student and reservation date must be provided.");
        }
        if (reservationRepository.findByStudent_Mail(reservation.getStudent().getMail()) != null) {
            throw new AlreadyExistsException(reservation.getStudent().getIndex());
        }

        Student student = studentRepository.findById(reservation.getStudent().getId()).get();
        Reservation newReservation = createReservationFromDTO(reservation, student);

        Optional<Thesis> thesisOptional = thesisRepository.findById(reservation.getThesisId());
        if (thesisOptional.isPresent()) {
            Thesis thesis = thesisOptional.get();

            updateThesisWhenReservationIsCreated(thesis, newReservation, student);

            sendMailDuringReservationCreation(thesis, newReservation, student);
        } else {
            throw new NotFoundException("Thesis with id " + reservation.getThesisId() + " does not exist.");
        }

        reservationRepository.saveAndFlush(newReservation);
        return newReservation;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<Reservation> addListReservation(List<ReservationDTO> reservations) {
        List<Reservation> newReservations = new ArrayList<>();
        for (ReservationDTO reservation : reservations) {
            if (reservation.getThesisId() == null || reservation.getStudent() == null
                    || reservation.getReservationDate() == null) {
                throw new IllegalArgumentException("Thesis, student and reservation date must be provided.");
            }
            if (reservationRepository.findByStudent_Mail(reservation.getStudent().getMail()) != null) {
                throw new AlreadyExistsException(reservation.getStudent().getIndex());
            }

            Student student = studentRepository.findById(reservation.getStudent().getId()).orElseThrow(() ->
                    new NotFoundException("Student with id " + reservation.getStudent().getId() + " does not exist."));
            Reservation newReservation = createReservationFromDTO(reservation, student);

            Optional<Thesis> thesisOptional = thesisRepository.findById(reservation.getThesisId());
            if (thesisOptional.isPresent()) {
                Thesis thesis = thesisOptional.get();

                updateThesisWhenReservationIsCreated(thesis, newReservation, student);

                sendMailDuringReservationCreation(thesis, newReservation, student);
            } else {
                throw new NotFoundException("Thesis with id " + reservation.getThesisId() + " does not exist.");
            }
            newReservations.add(newReservation);
        }
        reservationRepository.saveAllAndFlush(newReservations);
        return newReservations;
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

    @Transactional
    public Reservation updateReservation(Reservation newReservation, Long id) {

        return reservationRepository.findById(id)
                .map(reservation -> {
                    if (!reservation.isReadyForApproval() && newReservation.isReadyForApproval()) {
                        mailService.sendHtmlMailMessage(reservation.getThesis().getSupervisor().getMail(),
                                MailTemplates.RESERVATION_SENT_TO_SUPERVISOR,
                                reservation.getStudent(), reservation.getThesis().getSupervisor(),
                                reservation.getThesis());
                    }

                    if (!reservation.isConfirmedBySupervisor() && newReservation.isConfirmedBySupervisor()) {
                        reservation.getThesis().setStatus(statusRepository.findByName("Assigned").orElse(null));
                    }

                    reservation.setConfirmedByLeader(newReservation.isConfirmedByLeader());
                    reservation.setConfirmedBySupervisor(newReservation.isConfirmedBySupervisor());
                    reservation.setReadyForApproval(newReservation.isReadyForApproval());
                    reservation.setConfirmedByStudent(newReservation.isConfirmedByStudent());
                    reservation.setSentForApprovalDate(newReservation.getSentForApprovalDate());
                    return reservationRepository.save(reservation);
                })
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " does not exist."));
    }

    @Transactional
    public List<Reservation> updateReservationsForThesis(Long thesisId, List<Reservation> newReservations) {
        List<Reservation> reservations = reservationRepository.findByThesis(thesisRepository.findById(thesisId)
                .orElseThrow(() -> new NotFoundException("Thesis with id " + thesisId + " does not exist.")));
        for (Reservation res: reservations){
            for (Reservation newRes: newReservations){
                if (res.getId() == newRes.getId()){
                    if (!res.isReadyForApproval() && newRes.isReadyForApproval()) {
                        mailService.sendHtmlMailMessage(res.getThesis().getSupervisor().getMail(),
                                MailTemplates.RESERVATION_SENT_TO_SUPERVISOR,
                                res.getStudent(), res.getThesis().getSupervisor(),
                                res.getThesis());
                    }
                    
                    if (!res.isConfirmedBySupervisor() && newRes.isConfirmedBySupervisor()) {
                        res.getThesis().setStatus(statusRepository.findByName("Assigned").orElse(null));
                    }
                    
                    res.setConfirmedByLeader(newRes.isConfirmedByLeader());
                    res.setConfirmedBySupervisor(newRes.isConfirmedBySupervisor());
                    res.setReadyForApproval(newRes.isReadyForApproval());
                    res.setConfirmedByStudent(newRes.isConfirmedByStudent());
                    res.setSentForApprovalDate(newRes.getSentForApprovalDate());
                }
            }
        }
        reservationRepository.saveAll(reservations);
        return reservations;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Reservation deleteReservation(Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservationRepository.deleteById(id);
                    thesisRepository.findById(reservation.getThesis().getId())
                            .ifPresent(thesis -> {
                                if (thesis.getLeader() != null && Objects.equals(thesis.getLeader().getId(),
                                        reservation.getStudent().getId())) {
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
                                thesis.setOccupied(Math.max(thesis.getOccupied() - 1, 0));
                                if (thesis.getOccupied() == 0) {
                                    thesis.setLeader(null);
                                    if (thesis.getStatus() == statusRepository.findByName("Assigned").orElse(null)) {
                                        thesis.setStatus(statusRepository.findByName("Approved").orElse(null));
                                    }
                                }
                                thesisRepository.save(thesis);

                                mailService.sendHtmlMailMessage(reservation.getStudent().getMail(),
                                        MailTemplates.RESERVATION_CANCELED,
                                        reservation.getStudent(), null,
                                        reservation.getThesis());
                            });
                    return reservation;
                })
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " does not exist."));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Scheduled(cron = "0 0 3 * * ?") // every day at 3:00 AM
    public void removeExpiredReservations() {
        LocalDateTime threshold = now().minusDays(1).minusHours(3); // 1 day and 3 hours ago
        reservationRepository.findAll().stream()
                .filter(reservation -> !reservation.isConfirmedBySupervisor())
                .filter(reservation -> !reservation.isConfirmedByLeader() || !reservation.isConfirmedByStudent())
                .filter(reservation -> reservation.getReservationDate().isBefore(threshold))
                .forEach(reservation -> {
                    reservationRepository.delete(reservation);

                    mailService.sendHtmlMailMessage(reservation.getStudent().getMail(),
                            MailTemplates.RESERVATION_CANCELED,
                            reservation.getStudent(), null,
                            reservation.getThesis());

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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Scheduled(cron = "0 0 2 * * ?") // every day at 2:00 AM
    public void acceptReservationsMadeBySupervisor() {
        LocalDateTime threshold = now().minusDays(7).minusHours(2); // 7 day and 2 hours ago
        reservationRepository.findAll().stream()
                .filter(Reservation::isConfirmedBySupervisor)
                .filter(reservation -> !reservation.isConfirmedByStudent())
                .filter(reservation -> reservation.getReservationDate().isBefore(threshold))
                .forEach(reservation -> {
                    reservation.setConfirmedByStudent(true);
                    reservationRepository.save(reservation);
                });
    }

    private Reservation createReservationFromDTO(ReservationDTO reservation, Student student) {
        Reservation newReservation = new Reservation();
        newReservation.setConfirmedByLeader(reservation.isConfirmedByLeader());
        newReservation.setConfirmedBySupervisor(reservation.isConfirmedBySupervisor());
        newReservation.setConfirmedByStudent(reservation.isConfirmedByStudent());
        newReservation.setReadyForApproval(reservation.isReadyForApproval());
        newReservation.setReservationDate(reservation.getReservationDate());
        newReservation.setSentForApprovalDate(reservation.getSentForApprovalDate());
        newReservation.setStudent(student);
        return newReservation;
    }

    private void updateThesisWhenReservationIsCreated(Thesis thesis, Reservation newReservation, Student student) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authorities.stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            if (thesis.getOccupied() >= thesis.getNumPeople())
                throw new ThesisOccupancyFullException();
            if (thesis.getStatus() != statusRepository.findByName("Approved").orElse(null)) {
                throw new IllegalArgumentException("Thesis is not available.");
            }
        }

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

    private void sendMailDuringReservationCreation(Thesis thesis, Reservation newReservation, Student student) {
        if (newReservation.isConfirmedByLeader() && !newReservation.isConfirmedByStudent()) {
            mailService.sendHtmlMailMessage(student.getMail(), MailTemplates.RESERVATION_LEADER,
                    student, null, thesis);
        } else if (!newReservation.isConfirmedByLeader() && newReservation.isConfirmedByStudent()) {
            mailService.sendHtmlMailMessage(student.getMail(), MailTemplates.RESERVATION_STUDENT,
                    student, null, thesis);
        } else  if (newReservation.isConfirmedByLeader() && newReservation.isConfirmedByStudent()) {
            if (newReservation.isConfirmedBySupervisor()) {
                mailService.sendHtmlMailMessage(student.getMail(),
                        MailTemplates.RESERVATION_ADMIN, student, null, thesis);
            }
        }
    }
}
