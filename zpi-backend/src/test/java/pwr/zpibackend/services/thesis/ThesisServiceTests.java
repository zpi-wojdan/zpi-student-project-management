package pwr.zpibackend.services.thesis;

import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import pwr.zpibackend.dto.thesis.ThesisDTO;
import pwr.zpibackend.exceptions.LimitOfThesesReachedException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.thesis.ReservationRepository;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.repositories.user.StudentRepository;
import pwr.zpibackend.services.mailing.MailService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ThesisServiceTests {

    @Mock
    private ThesisRepository thesisRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private StudyCycleRepository studyCycleRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private MailService mailService;

    @InjectMocks
    private ThesisService thesisService;

    private List<Thesis> theses;
    private Thesis thesis;
    private List<Thesis> publicTheses;
    private List<Status> statuses;
    private Sort sort = Sort.by(Sort.Direction.DESC, "studyCycle.name", "id");

    @BeforeEach
    public void setUp() {
        statuses = new ArrayList<>();
        statuses.add(new Status(1, "Draft"));
        statuses.add(new Status(2, "Pending approval"));
        statuses.add(new Status(3, "Rejected"));
        statuses.add(new Status(4, "Approved"));
        statuses.add(new Status(5, "Assigned"));
        statuses.add(new Status(6, "Closed"));

        thesis = new Thesis();
        thesis.setId(1L);
        thesis.setNamePL("Thesis 1 PL");
        thesis.setNameEN("Thesis 1 EN");
        thesis.setDescriptionPL("Description 1");
        thesis.setDescriptionEN("Description 1");
        thesis.setNumPeople(4);
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setNumTheses(2);
        thesis.setSupervisor(emp);
        thesis.setPrograms(List.of(new Program()));
        thesis.setStudyCycle(new StudyCycle());
        thesis.setStatus(new Status(1, "Draft"));
        thesis.setStatus(statuses.get(0));

        Thesis thesis2 = new Thesis();
        thesis2.setId(2L);
        thesis2.setNamePL("Thesis 2 PL");
        thesis2.setNameEN("Thesis 2 EN");
        thesis2.setDescriptionPL("Description 2");
        thesis2.setDescriptionEN("Description 2");
        thesis2.setNumPeople(4);
        thesis2.setSupervisor(emp);
        thesis2.setPrograms(List.of(new Program()));
        thesis2.setStatus(statuses.get(1));

        Thesis thesis3 = new Thesis();
        thesis3.setId(3L);
        thesis3.setNamePL("Thesis 3 PL");
        thesis3.setNameEN("Thesis 3 EN");
        thesis3.setDescriptionPL("Description 3");
        thesis3.setDescriptionEN("Description 3");
        thesis3.setNumPeople(4);
        thesis3.setSupervisor(emp);
        thesis3.setPrograms(List.of(new Program()));
        thesis3.setStatus(statuses.get(2));

        Thesis thesis4 = new Thesis();
        thesis4.setId(4L);
        thesis4.setNamePL("Thesis 4 PL");
        thesis4.setNameEN("Thesis 4 EN");
        thesis4.setDescriptionPL("Description 4");
        thesis4.setDescriptionEN("Description 4");
        thesis4.setNumPeople(4);
        thesis4.setSupervisor(emp);
        thesis4.setPrograms(List.of(new Program()));
        thesis4.setStatus(statuses.get(3));

        Thesis thesis5 = new Thesis();
        thesis5.setId(5L);
        thesis5.setNamePL("Thesis 5 PL");
        thesis5.setNameEN("Thesis 5 EN");
        thesis5.setDescriptionPL("Description 5");
        thesis5.setDescriptionEN("Description 5");
        thesis5.setNumPeople(4);
        thesis5.setSupervisor(emp);
        thesis5.setPrograms(List.of(new Program()));
        thesis5.setStatus(statuses.get(4));

        Thesis thesis6 = new Thesis();
        thesis6.setId(6L);
        thesis6.setNamePL("Thesis 6 PL");
        thesis6.setNameEN("Thesis 6 EN");
        thesis6.setDescriptionPL("Description 6");
        thesis6.setDescriptionEN("Description 6");
        thesis6.setNumPeople(4);
        thesis6.setSupervisor(emp);
        thesis6.setPrograms(List.of(new Program()));
        thesis6.setStatus(statuses.get(5));

        theses = new ArrayList<>();
        theses.add(thesis6);
        theses.add(thesis5);
        theses.add(thesis4);
        theses.add(thesis3);
        theses.add(thesis2);
        theses.add(thesis);

        publicTheses = new ArrayList<>();
        publicTheses.add(thesis6);
        publicTheses.add(thesis5);
        publicTheses.add(thesis4);
    }

    @Test
    public void testGetAllTheses() {

        when(thesisRepository.findAllByOrderByStudyCycleNameDescIdDesc()).thenReturn(theses);

        List<Thesis> result = thesisService.getAllTheses();

        assertEquals(6, result.size());
        assertEquals(theses, result);
    }

    @Test
    public void testGetAllPublicTheses() {
        List <String> statusNames = List.of("Approved", "Assigned", "Closed");
        when(thesisRepository.findAllByStatusNameIn(statusNames, sort)).thenReturn(publicTheses);

        List<Thesis> result = thesisService.getAllPublicTheses();

        assertEquals(3, result.size());
        assertEquals(publicTheses, result);
    }

    @Test
    public void testGetThesisById() throws NotFoundException {
        Long thesisId = 1L;

        when(thesisRepository.findById(thesisId)).thenReturn(Optional.of(thesis));

        Thesis result = thesisService.getThesis(thesisId);

        assertEquals(thesis, result);
    }

    @Test
    public void testGetThesisByIdNotFound() {
        Long thesisId = 1L;

        when(thesisRepository.findById(thesisId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> thesisService.getThesis(thesisId));
    }

    @Test
    public void testAddThesis() throws NotFoundException {
        ThesisDTO thesisDTO = new ThesisDTO();
        Program program = mock(Program.class);
        StudyCycle studyCycle = mock(StudyCycle.class);

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setNumTheses(2);

        Student student = new Student();
        student.setIndex("123456");
        student.setMail("123456@mail.com");

        Reservation reservation = new Reservation();
        reservation.setStudent(student);
        reservation.setThesis(thesis);
        reservation.setConfirmedByLeader(false);
        reservation.setConfirmedBySupervisor(true);
        reservation.setConfirmedByStudent(false);
        reservation.setReadyForApproval(true);


        when(employeeRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(employee));
        when(thesisRepository.saveAndFlush(any(Thesis.class))).thenAnswer(invocation -> {
            Thesis savedThesis = invocation.getArgument(0);
            savedThesis.setId(1L);
            return savedThesis;
        });
        when(programRepository.findById(any(Long.class))).thenReturn(Optional.of(program));
        when(studyCycleRepository.findById(any(Long.class))).thenReturn(Optional.of(studyCycle));
        when(statusRepository.findByName("Draft")).thenReturn(Optional.of(statuses.get(0)));
        when(statusRepository.findByName("Rejected")).thenReturn(Optional.of(statuses.get(2)));
        when(statusRepository.findByName("Closed")).thenReturn(Optional.of(statuses.get(5)));
        when(thesisRepository.findAllBySupervisor_IdAndStatus_NameIn(employee.getId(),
                List.of("Pending approval", "Approved", "Assigned"), sort)).thenReturn(Collections.emptyList());
        when(statusRepository.findById(any(Long.class))).thenReturn(Optional.of(statuses.get(1)));
        when(studentRepository.findByIndex(student.getIndex())).thenReturn(Optional.of(student));
        when(reservationRepository.findByStudent_Mail(student.getMail())).thenReturn(null);
        when(reservationRepository.saveAndFlush(any(Reservation.class))).thenReturn(reservation);

        thesis.setId(1L);
        thesis.setNamePL("Thesis 1 PL");
        thesis.setNameEN("Thesis 1 EN");
        thesis.setDescriptionPL("Description 1");
        thesis.setDescriptionEN("Description 1");
        thesis.setNumPeople(4);
        thesis.setOccupied(4);
        thesis.setSupervisor(employee);
        thesis.setPrograms(List.of(program));
        thesis.setStatus(statuses.get(1));
        thesis.setStudyCycle(studyCycle);

        thesisDTO.setNamePL("Thesis 1 PL");
        thesisDTO.setNameEN("Thesis 1 EN");
        thesisDTO.setDescriptionPL("Description 1");
        thesisDTO.setDescriptionEN("Description 1");
        thesisDTO.setNumPeople(4);
        thesisDTO.setProgramIds(List.of(1L));
        thesisDTO.setStudyCycleId(Optional.of(1L));
        thesisDTO.setStatusId(2L);
        thesisDTO.setSupervisorId(1L);
        thesisDTO.setStudentIndexes(List.of("123456"));

        Thesis result = thesisService.addThesis(thesisDTO);

        verify(thesisRepository, times(2)).saveAndFlush(any(Thesis.class));
        assertEquals(thesis, result);

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).saveAndFlush(reservationCaptor.capture());
        Reservation savedReservation = reservationCaptor.getValue();

        assertEquals(student, savedReservation.getStudent());
        assertEquals(thesis, savedReservation.getThesis());
        assertFalse(savedReservation.isConfirmedByLeader());
        assertTrue(savedReservation.isConfirmedBySupervisor());
        assertFalse(savedReservation.isConfirmedByStudent());
        assertTrue(savedReservation.isReadyForApproval());
        assertEquals(thesis, result);
    }

    @Test
    public void testUpdateThesis() {
        Long thesisId = 1L;
        ThesisDTO thesisDTO = new ThesisDTO();
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setNumTheses(2);

        Program program = mock(Program.class);
        StudyCycle studyCycle = mock(StudyCycle.class);

        when(thesisRepository.existsById(thesisId)).thenReturn(true);
        when(thesisRepository.findById(thesisId)).thenReturn(Optional.of(thesis));
        when(employeeRepository.findById(any(Long.class))).thenReturn(Optional.of(employee));
        when(thesisRepository.saveAndFlush(any(Thesis.class))).thenAnswer(invocation -> {
            Thesis savedThesis = invocation.getArgument(0);
            savedThesis.setId(1L);
            return savedThesis;
        });
        when(programRepository.findById(any(Long.class))).thenReturn(Optional.of(program));
        when(studyCycleRepository.findById(any(Long.class))).thenReturn(Optional.of(studyCycle));
        when(statusRepository.findByName("Draft")).thenReturn(Optional.of(statuses.get(0)));
        when(statusRepository.findByName("Rejected")).thenReturn(Optional.of(statuses.get(2)));
        when(statusRepository.findByName("Closed")).thenReturn(Optional.of(statuses.get(5)));
        when(thesisRepository.findAllBySupervisor_IdAndStatus_NameIn(employee.getId(),
                List.of("Pending approval", "Approved", "Assigned"), sort)).thenReturn(Collections.emptyList());
        when(statusRepository.findById(any(Long.class))).thenReturn(Optional.of(statuses.get(1)));

        Thesis updatedThesis = new Thesis();
        updatedThesis.setId(1L);
        updatedThesis.setNamePL("Thesis 1 PL");
        updatedThesis.setNameEN("Thesis 1 EN");
        updatedThesis.setDescriptionPL("Description 1");
        updatedThesis.setDescriptionEN("Description 1");
        updatedThesis.setNumPeople(4);
        updatedThesis.setSupervisor(employee);
        updatedThesis.setPrograms(List.of(program));
        updatedThesis.setStatus(statuses.get(1));
        updatedThesis.setStudyCycle(studyCycle);

        thesisDTO.setNamePL("Thesis 1 PL");
        thesisDTO.setNameEN("Thesis 1 EN");
        thesisDTO.setDescriptionPL("Description 1");
        thesisDTO.setDescriptionEN("Description 1");
        thesisDTO.setNumPeople(4);
        thesisDTO.setProgramIds(List.of(1L));
        thesisDTO.setStudyCycleId(Optional.of(1L));
        thesisDTO.setStatusId(2L);
        thesisDTO.setSupervisorId(1L);

        Thesis result = thesisService.updateThesis(thesisId, thesisDTO);
        assertEquals(updatedThesis, result);
    }

    @Test
    public void testUpdateThesisNotFound() {
        Long thesisId = 1L;
        ThesisDTO thesisDTO = mock(ThesisDTO.class);
        when(thesisRepository.findById(thesisId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> thesisService.updateThesis(thesisId, thesisDTO));
    }

    @Test
    public void testDeleteThesis() throws NotFoundException {
        Long thesisId = 1L;
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setSupervisedTheses(new ArrayList<>());
        employee.getSupervisedTheses().add(thesis);

        thesis.setSupervisor(employee);

        Thesis deletedThesis = new Thesis();
        deletedThesis.setId(thesis.getId());
        deletedThesis.setNamePL(thesis.getNamePL());
        deletedThesis.setNameEN(thesis.getNameEN());
        deletedThesis.setDescriptionPL(thesis.getDescriptionPL());
        deletedThesis.setDescriptionEN(thesis.getDescriptionEN());
        deletedThesis.setNumPeople(thesis.getNumPeople());
        deletedThesis.setOccupied(thesis.getOccupied());
        deletedThesis.setSupervisor(null);
        deletedThesis.setPrograms(null);
        deletedThesis.setStatus(null);
        deletedThesis.setStudyCycle(null);
        deletedThesis.setLeader(null);

        when(thesisRepository.findById(thesisId)).thenReturn(Optional.of(thesis));

        Thesis result = thesisService.deleteThesis(thesisId);

        assertEquals(deletedThesis, result);
        assertEquals(0, employee.getSupervisedTheses().size());
    }

    @Test
    public void testDeleteThesisNotFound() {
        Long thesisId = 1L;
        when(thesisRepository.findById(thesisId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> thesisService.deleteThesis(thesisId));
    }

    @Test
    public void testGetAllThesesByStatusId() {
        String statusName = "Draft";
        when(thesisRepository.findAllByStatusName(statusName, sort)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesByStatusName(statusName);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesExcludingStatusName() throws NotFoundException {
        String statusName = "Draft";
        when(thesisRepository.findAllByOrderByStudyCycleNameDescIdDesc()).thenReturn(
                theses.stream()
                        .filter(thesis -> !statusName.equals(thesis.getStatus().getName()))
                        .collect(Collectors.toList()));
        when(statusRepository.findByName(statusName)).thenReturn(Optional.of(new Status("Draft")));

        List<Thesis> result = thesisService.getAllThesesExcludingStatusName(statusName);
        verify(statusRepository).findByName(statusName);
        verify(thesisRepository).findAllByOrderByStudyCycleNameDescIdDesc();

        List<Thesis> expectedResult = theses.stream()
                .filter(thesis -> !statusName.equals(thesis.getStatus().getName()))
                .collect(Collectors.toList());
        assertEquals(expectedResult, result);
    }

    @Test
    public void testGetAllThesesExcludingStatusIdNotFound() {
        String statusName = "Draft";
        when(statusRepository.findByName(statusName)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> thesisService.getAllThesesExcludingStatusName(statusName));
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusId() {
        Long empId = 1L;
        String statName = "Draft";
        when(thesisRepository.findAllBySupervisorIdAndStatusName(empId, statName, sort)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesForEmployeeByStatusName(empId, statName);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusIdNotFound() {
        Long empId = 1L;
        String statName = "Draft";
        when(thesisRepository.findAllBySupervisorIdAndStatusName(empId, statName, sort)).thenReturn(Collections.emptyList());

        assertEquals(thesisService.getAllThesesForEmployeeByStatusName(empId, statName), Collections.emptyList());
    }


    @Test
    public void testGetAllThesesForEmployee() {
        Long empId = 1L;
        when(thesisRepository.findAllBySupervisorId(empId, sort)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesForEmployee(empId);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesForEmployeeNotFound() {
        Long empId = 1L;
        when(thesisRepository.findAllBySupervisorId(empId, sort)).thenReturn(Collections.emptyList());

        assertEquals(thesisService.getAllThesesForEmployee(empId), Collections.emptyList());
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusNameList(){
        Long empId = 1L;
        List<String> statName = Arrays.asList("Draft", "Rejected");
        when(thesisRepository.findAllBySupervisor_IdAndStatus_NameIn(empId, statName, sort)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesForEmployeeByStatusNameList(empId, statName);
        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusNameListNotFound(){
        Long empId = 5L;
        List<String> statName = Arrays.asList("Draft", "Rejected");
        when(thesisRepository.findAllBySupervisor_IdAndStatus_NameIn(empId, statName, sort)).thenReturn(Collections.emptyList());

        assertEquals(thesisService.getAllThesesForEmployeeByStatusNameList(empId, statName), Collections.emptyList());
    }

    @Test
    public void testUpdateThesesStatusInBulkApproved(){
        String statName = "Approved";
        List<Long> thesesIds = List.of(6L);
        Status newStatus = new Status(2L, statName);

        List<Thesis> updatedTheses = new ArrayList<>();
        updatedTheses.add(theses.get(0));
        for (Thesis up : updatedTheses){
            up.setStatus(newStatus);
        }

        when(statusRepository.findByName(statName)).thenReturn(Optional.of(newStatus));
        when(thesisRepository.findById(thesesIds.get(0))).thenReturn(Optional.of(theses.get(0)));
        when(thesisRepository.saveAllAndFlush(anyList())).thenReturn(updatedTheses);

        List<Thesis> result = thesisService.updateThesesStatusInBulk(statName, thesesIds);
        assertEquals(updatedTheses, result);
    }

    @Test
    public void testUpdateThesesStatusInBulkRejected() {
        String statName = "Rejected";
        List<Long> thesesIds = List.of(6L);
        Status newStatus = new Status(3L, statName);

        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());

        Thesis thesisWithReservations = new Thesis();
        thesisWithReservations.setId(6L);
        thesisWithReservations.setReservations(reservations);
        thesisWithReservations.setStatus(newStatus);

        List<Thesis> updatedTheses = new ArrayList<>();
        updatedTheses.add(thesisWithReservations);
        for (Thesis up : updatedTheses){
            up.setStatus(newStatus);
        }

        when(statusRepository.findByName(statName)).thenReturn(Optional.of(newStatus));
        when(thesisRepository.findById(thesesIds.get(0))).thenReturn(Optional.of(thesisWithReservations));
        when(thesisRepository.saveAllAndFlush(anyList())).thenReturn(updatedTheses);
        doNothing().when(reservationRepository).deleteAll(anyList());

        List<Thesis> result = thesisService.updateThesesStatusInBulk(statName, thesesIds);

        assertEquals(updatedTheses, result);
    }


    @Test
    public void testUpdateThesesStatusInBulkNotFound(){
        String statName = "foo";
        List<Long> thesesIds = mock(List.class);

        when(statusRepository.findByName(statName)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> thesisService.updateThesesStatusInBulk(statName, thesesIds));
    }    @Test
    public void testAddThesisNotFoundException() {
        ThesisDTO thesisDTO = new ThesisDTO();
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setNumTheses(2);

        when(employeeRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(employee));
        when(thesisRepository.findAllBySupervisor_IdAndStatus_NameIn(eq(employee.getId()),
                anyList(), any(Sort.class))).thenReturn(Arrays.asList(
                new Thesis(), new Thesis()));

        thesisDTO.setSupervisorId(1L);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> thesisService.addThesis(thesisDTO));

    }

    @Test
    public void testUpdateThesisNotFoundException() {
        Long thesisId = 1L;
        ThesisDTO thesisDTO = mock(ThesisDTO.class);

        when(thesisRepository.existsById(thesisId)).thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> thesisService.updateThesis(thesisId, thesisDTO));

        assertEquals("Thesis with id 1 does not exist", exception.getMessage());
    }

    @Test
    public void testGetThesisByStudentId() {
        Long studentId = 1L;

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(reservationRepository.findByStudentId(studentId)).thenReturn(reservation);

        Thesis thesis = new Thesis();
        thesis.setId(2L);
        when(thesisRepository.findByReservations_Id(reservation.getId())).thenReturn(Optional.of(thesis));

        Thesis result = thesisService.getThesisByStudentId(studentId);

        assertEquals(thesis, result);

        verify(reservationRepository, times(1)).findByStudentId(studentId);
        verify(thesisRepository, times(1)).findByReservations_Id(reservation.getId());
    }

    @Test
    public void testGetThesisByStudentIdReservationNotFound() {
        Long studentId = 1L;

        when(reservationRepository.findByStudentId(studentId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> thesisService.getThesisByStudentId(studentId));

        verify(reservationRepository, times(1)).findByStudentId(studentId);
        verify(thesisRepository, never()).findByReservations_Id(anyLong());
    }
}
