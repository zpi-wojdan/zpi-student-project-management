package pwr.zpibackend.services.impl.thesis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pwr.zpibackend.dto.thesis.ReservationDTO;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.services.impl.mailing.MailService;
import pwr.zpibackend.services.impl.thesis.ReservationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ReservationServiceTests {
    @Mock
    private ThesisRepository thesisRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private Authentication authentication = mock(Authentication.class);
    @Mock
    private MailService mailService;
    @InjectMocks
    private ReservationService reservationService;

    private List<Thesis> thesisList;
    private List<Student> students;
    private List<Status> statuses;
    private List<Reservation> reservations;
    private Reservation reservation;
    private ReservationDTO reservationDTO;

    @BeforeEach
    public void setUp(){
        statuses = new ArrayList<>();
        statuses.add(new Status(1, "Draft"));
        statuses.add(new Status(2, "Pending approval"));
        statuses.add(new Status(3, "Rejected"));
        statuses.add(new Status(4, "Approved"));
        statuses.add(new Status(5, "Assigned"));
        statuses.add(new Status(6, "Closed"));

        Thesis thesis = new Thesis();
        thesis.setId(1L);
        thesis.setNamePL("Thesis 1 PL");
        thesis.setNameEN("Thesis 1 EN");
        thesis.setNumPeople(4);
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setNumTheses(2);
        thesis.setSupervisor(emp);
        thesis.setPrograms(List.of(new Program()));
        thesis.setStudyCycle(new StudyCycle());
        thesis.setStatus(statuses.get(0));

        Thesis thesis2 = new Thesis();
        thesis2.setId(2L);
        thesis2.setNamePL("Thesis 2 PL");
        thesis2.setNameEN("Thesis 2 EN");
        thesis2.setNumPeople(4);
        thesis2.setSupervisor(emp);
        thesis2.setPrograms(List.of(new Program()));
        thesis2.setStatus(statuses.get(1));

        Thesis thesis3 = new Thesis();
        thesis3.setId(3L);
        thesis3.setNamePL("Thesis 3 PL");
        thesis3.setNameEN("Thesis 3 EN");
        thesis3.setNumPeople(4);
        thesis3.setSupervisor(emp);
        thesis3.setPrograms(List.of(new Program()));
        thesis3.setStatus(statuses.get(2));

        Thesis thesis4 = new Thesis();
        thesis4.setId(4L);
        thesis4.setNamePL("Thesis 4 PL");
        thesis4.setNameEN("Thesis 4 EN");
        thesis4.setNumPeople(4);
        thesis4.setSupervisor(emp);
        thesis4.setPrograms(List.of(new Program()));
        thesis4.setStatus(statuses.get(3));

        Thesis thesis5 = new Thesis();
        thesis5.setId(5L);
        thesis5.setNamePL("Thesis 5 PL");
        thesis5.setNameEN("Thesis 5 EN");
        thesis5.setNumPeople(4);
        thesis5.setSupervisor(emp);
        thesis5.setPrograms(List.of(new Program()));
        thesis5.setStatus(statuses.get(4));

        Thesis thesis6 = new Thesis();
        thesis6.setId(6L);
        thesis6.setNamePL("Thesis 6 PL");
        thesis6.setNameEN("Thesis 6 EN");
        thesis6.setNumPeople(4);
        thesis6.setSupervisor(emp);
        thesis6.setPrograms(List.of(new Program()));
        thesis6.setStatus(statuses.get(5));

        thesisList = List.of(thesis, thesis2, thesis3, thesis4, thesis5, thesis6);

        Student student = new Student();
        student.setId(1L);
        student.setIndex("123456");
        student.setName("Student 1");
        student.setSurname("Student 1");
        student.setMail("123456@student.pwr.edu.pl");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setIndex("123457");
        student2.setName("Student 2");
        student2.setSurname("Student 2");
        student2.setMail("123457@student.pwr.edu.pl");

        Student student3 = new Student();
        student3.setId(3L);
        student3.setIndex("123458");
        student3.setName("Student 3");
        student3.setSurname("Student 3");
        student3.setMail("123458@student.pwr.edu.pl");

        students = List.of(student, student2, student3);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setThesis(thesis);
        reservation.setStudent(student);
        reservation.setReservationDate(LocalDateTime.parse("2023-10-05T12:34:56"));

        reservations = List.of(reservation);

        reservationDTO = new ReservationDTO();
        reservationDTO.setReservationDate(LocalDateTime.parse("2023-10-05T12:34:56"));
        reservationDTO.setThesisId(1L);
        reservationDTO.setStudent(student);
    }

    @Test
    public void testGetAllReservations(){
        when(reservationRepository.findAll()).thenReturn(reservations);
        List<Reservation> results = reservationService.getAllReservations();
        assert results.size() == 1;
        assert results.get(0).getId() == reservation.getId();
    }

    @Test
    public void testGetReservationById(){
        when(reservationRepository.findById(1L)).thenReturn(ofNullable(reservation));
        Reservation result = reservationService.getReservation(1L);
        assert result.getId() == reservation.getId();
    }

    @Test
    public void testGetReservationByIdThrows(){
        when(reservationRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(NotFoundException.class, () -> reservationService.getReservation(1L));
    }

    @Test
    public void testAddReservationThesisNotApproved(){
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(statusRepository.findByName("Draft")).thenReturn(ofNullable(statuses.get(0)));
        when(thesisRepository.findById(1L)).thenReturn(ofNullable(thesisList.get(0)));
        when(studentRepository.findById(1L)).thenReturn(ofNullable(students.get(0)));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(reservationDTO));
    }

    @Test
    public void testAddReservationNullFields(){
        reservationDTO.setThesisId(null);
        reservationDTO.setStudent(null);
        reservationDTO.setReservationDate(null);
        assertThrows(IllegalArgumentException.class, () -> reservationService.addReservation(reservationDTO));
    }

    @Test
    public void testAddReservationStudentAlreadyHasAReservation(){
        when(reservationRepository.findByStudent_Mail(students.get(0).getMail())).thenReturn(reservations.get(0));
        assertThrows(AlreadyExistsException.class, () -> reservationService.addReservation(reservationDTO));
    }

    @Test
    public void testAddReservationThesisWithNoStudents() {
        reservationDTO.setThesisId(4L);
        reservation.setThesis(thesisList.get(3));
        reservation.setConfirmedByStudent(true);
        thesisList.get(3).setReservations(new ArrayList<>());
        thesisList.get(3).setOccupied(0);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(statusRepository.findByName("Approved")).thenReturn(ofNullable(statuses.get(3)));
        when(thesisRepository.findById(4L)).thenReturn(ofNullable(thesisList.get(3)));
        when(studentRepository.findById(1L)).thenReturn(ofNullable(students.get(0)));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation result = reservationService.addReservation(reservationDTO);
        assert result.getThesis().getId() == reservation.getThesis().getId();
        assert Objects.equals(result.getStudent().getId(), reservation.getStudent().getId());
        assert result.getReservationDate().isEqual(reservation.getReservationDate());
    }

    @Test
    public void testAddReservationThesisWithNoThesis(){
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(statusRepository.findByName("Approved")).thenReturn(ofNullable(statuses.get(3)));
        when(thesisRepository.findById(4L)).thenReturn(empty());
        when(studentRepository.findById(1L)).thenReturn(ofNullable(students.get(0)));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        assertThrows(NotFoundException.class, () -> reservationService.addReservation(reservationDTO));
    }

    @Test
    public void testAddReservationThesisWithStudents() {
        reservationDTO.setThesisId(4L);
        reservationDTO.setConfirmedByStudent(false);
        reservationDTO.setConfirmedByLeader(true);
        reservation.setThesis(thesisList.get(3));
        reservation.setConfirmedByStudent(false);
        reservation.setConfirmedByLeader(true);
        thesisList.get(3).setReservations(new ArrayList<>());
        thesisList.get(3).setOccupied(1);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(statusRepository.findByName("Approved")).thenReturn(ofNullable(statuses.get(3)));
        when(thesisRepository.findById(4L)).thenReturn(ofNullable(thesisList.get(3)));
        when(studentRepository.findById(1L)).thenReturn(ofNullable(students.get(0)));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        Reservation result = reservationService.addReservation(reservationDTO);
        assert result.getThesis().getId() == reservation.getThesis().getId();
        assert Objects.equals(result.getStudent().getId(), reservation.getStudent().getId());
        assert result.getReservationDate().isEqual(reservation.getReservationDate());
    }

    @Test
    public void testUpdateReservation(){
        when(reservationRepository.findById(1L)).thenReturn(ofNullable(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        Reservation result = reservationService.updateReservation(reservation, 1L);
        assert result.getId() == reservation.getId();
    }

    @Test
    public void testUpdateReservationNotFound(){
        when(reservationRepository.findById(1L)).thenReturn(empty());
        assertThrows(NotFoundException.class, () -> reservationService.updateReservation(reservation, 1L));
    }

    @Test
    public void testUpdateReservationReadyForApproval(){
        reservation.setReadyForApproval(false);
        Reservation updatedReservation = new Reservation();
        updatedReservation.setReadyForApproval(true);
        when(reservationRepository.findById(1L)).thenReturn(ofNullable(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        when(reservationRepository.findById(1L)).thenReturn(ofNullable(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        Reservation result = reservationService.updateReservation(updatedReservation, 1L);
        assert result.getId() == reservation.getId();
    }

    @Test
    public void testDeleteReservation(){
        thesisList.get(3).setLeader(students.get(0));
        reservation.setThesis(thesisList.get(3));

        when(reservationRepository.findById(1L)).thenReturn(ofNullable(reservation));
        when(thesisRepository.findById(4L)).thenReturn(ofNullable(thesisList.get(3)));
        reservationService.deleteReservation(1L);
    }

    @Test
    public void testDeleteReservationNotFound(){
        when(reservationRepository.findById(1L)).thenReturn(empty());
        assertThrows(NotFoundException.class, () -> reservationService.deleteReservation(1L));
    }

    @Test
    public void testAcceptReservationsMadeBySupervisor(){
        reservation.setThesis(thesisList.get(3));
        thesisList.get(3).setOccupied(1);
        reservations.forEach(res -> res.setConfirmedBySupervisor(true));
        reservations.forEach(res -> res.setConfirmedByStudent(false));
        reservations.forEach(res -> res.setReservationDate(LocalDateTime.parse("2023-10-05T12:34:56")));

        when(reservationRepository.findAll()).thenReturn(reservations);
        when(thesisRepository.findById(4L)).thenReturn(ofNullable(thesisList.get(3)));
        reservationService.acceptReservationsMadeBySupervisor();
    }

    @Test
    public void testRemoveExpiredReservations(){
        reservation.setThesis(thesisList.get(3));
        thesisList.get(3).setOccupied(1);
        when(reservationRepository.findAll()).thenReturn(reservations);
        when(thesisRepository.findById(4L)).thenReturn(ofNullable(thesisList.get(3)));
        reservationService.removeExpiredReservations();
    }
}
