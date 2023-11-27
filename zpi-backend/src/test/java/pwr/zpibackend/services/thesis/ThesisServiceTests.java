package pwr.zpibackend.services.thesis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pwr.zpibackend.dto.thesis.ThesisDTO;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @InjectMocks
    private ThesisService thesisService;

    private List<Thesis> theses;
    private Thesis thesis;

    @BeforeEach
    public void setUp() {
        thesis = new Thesis();
        thesis.setId(1L);
        thesis.setNamePL("Thesis 1 PL");
        thesis.setNameEN("Thesis 1 EN");
        thesis.setDescriptionPL("Description 1");
        thesis.setDescriptionEN("Description 1");
        thesis.setNumPeople(4);
        Employee emp = new Employee();
        emp.setId(1L);
        thesis.setSupervisor(emp);
        thesis.setPrograms(List.of(new Program()));
        thesis.setStudyCycle(new StudyCycle());
        thesis.setStatus(new Status(1, "Draft"));

        theses = new ArrayList<>();
        theses.add(thesis);
    }

    @Test
    public void testGetAllTheses() {

        when(thesisRepository.findAll()).thenReturn(theses);

        List<Thesis> result = thesisService.getAllTheses();

        assertEquals(1, result.size());
        assertEquals(theses, result);
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
        Employee employee = mock(Employee.class);
        Program program = mock(Program.class);
        Status status = mock(Status.class);
        StudyCycle studyCycle = mock(StudyCycle.class);


        when(employeeRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(employee));
        when(thesisRepository.saveAndFlush(any(Thesis.class))).thenAnswer(invocation -> {
            Thesis savedThesis = invocation.getArgument(0);
            savedThesis.setId(1L);
            return savedThesis;
        });
        when(programRepository.findById(any(Long.class))).thenReturn(Optional.of(program));
        when(statusRepository.findById(any(Long.class))).thenReturn(Optional.of(status));
        when(studyCycleRepository.findById(any(Long.class))).thenReturn(Optional.of(studyCycle));

        thesis.setId(1L);
        thesis.setNamePL("Thesis 1 PL");
        thesis.setNameEN("Thesis 1 EN");
        thesis.setDescriptionPL("Description 1");
        thesis.setDescriptionEN("Description 1");
        thesis.setNumPeople(4);
        thesis.setSupervisor(employee);
        thesis.setPrograms(List.of(program));
        thesis.setStatus(status);
        thesis.setStudyCycle(studyCycle);

        thesisDTO.setNamePL("Thesis 1 PL");
        thesisDTO.setNameEN("Thesis 1 EN");
        thesisDTO.setDescriptionPL("Description 1");
        thesisDTO.setDescriptionEN("Description 1");
        thesisDTO.setNumPeople(4);
        thesisDTO.setProgramIds(List.of(1L));
        thesisDTO.setStudyCycleId(Optional.of(1L));
        thesisDTO.setStatusId(1L);
        thesisDTO.setSupervisorId(1L);

        Thesis result = thesisService.addThesis(thesisDTO);

        verify(thesisRepository).saveAndFlush(any(Thesis.class));

        assertEquals(thesis, result);
    }



    @Test
    public void testUpdateThesis() throws NotFoundException {
        Long thesisId = 1L;
        ThesisDTO thesisDTO = new ThesisDTO();
        Employee employee = mock(Employee.class);
        Program program = mock(Program.class);
        Status status = mock(Status.class);
        StudyCycle studyCycle = mock(StudyCycle.class);

        when(thesisRepository.existsById(thesisId)).thenReturn(true);
        when(thesisRepository.findById(thesisId)).thenReturn(Optional.of(thesis));
        when(employeeRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(employee));
        when(thesisRepository.saveAndFlush(any(Thesis.class))).thenAnswer(invocation -> {
            Thesis savedThesis = invocation.getArgument(0);
            savedThesis.setId(1L);
            return savedThesis;
        });
        when(programRepository.findById(any(Long.class))).thenReturn(Optional.of(program));
        when(statusRepository.findById(any(Long.class))).thenReturn(Optional.of(status));
        when(studyCycleRepository.findById(any(Long.class))).thenReturn(Optional.of(studyCycle));

        thesis.setId(1L);
        thesis.setNamePL("Thesis 1 PL");
        thesis.setNameEN("Thesis 1 EN");
        thesis.setDescriptionPL("Description 1");
        thesis.setDescriptionEN("Description 1");
        thesis.setNumPeople(4);
        thesis.setSupervisor(employee);
        thesis.setPrograms(List.of(program));
        thesis.setStatus(status);
        thesis.setStudyCycle(studyCycle);

        thesisDTO.setNamePL("Thesis 1 PL");
        thesisDTO.setNameEN("Thesis 1 EN");
        thesisDTO.setDescriptionPL("Description 1");
        thesisDTO.setDescriptionEN("Description 1");
        thesisDTO.setNumPeople(4);
        thesisDTO.setProgramIds(List.of(1L));
        thesisDTO.setStudyCycleId(Optional.of(1L));
        thesisDTO.setStatusId(1L);
        thesisDTO.setSupervisorId(1L);

        Thesis result = thesisService.updateThesis(thesisId, thesisDTO);
        assertEquals(thesis, result);
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
        Thesis thesisToDelete = new Thesis();
        when(thesisRepository.findById(thesisId)).thenReturn(Optional.of(thesisToDelete));
        when(commentRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(commentRepository).deleteById(anyLong());
        doNothing().when(thesisRepository).deleteById(thesisId);

        Thesis result = thesisService.deleteThesis(thesisId);
        assertEquals(thesisToDelete, result);
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
        when(thesisRepository.findAllByStatusName(statusName)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesByStatusName(statusName);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesExcludingStatusId() throws NotFoundException {
        String statusName = "Draft";
        when(thesisRepository.findAll()).thenReturn(new ArrayList<>());
        when(statusRepository.findByName(statusName)).thenReturn(Optional.of(new Status("Draft")));

        List<Thesis> result = thesisService.getAllThesesExcludingStatusName(statusName);
        verify(statusRepository).findByName(statusName);
        verify(thesisRepository).findAll();

        List<Thesis> expectedResult = theses.stream()
                .filter(thesis -> !statusName.equals(thesis.getStatus().getName()))
                .collect(Collectors.toList());
        assertEquals(expectedResult, new ArrayList<>());
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
        when(thesisRepository.findAllByEmployeeIdAndStatusName(empId, statName)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesForEmployeeByStatusName(empId, statName);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusIdNotFound() {
        Long empId = 1L;
        String statName = "Draft";
        when(thesisRepository.findAllByEmployeeIdAndStatusName(empId, statName)).thenReturn(Collections.emptyList());

        assertEquals(thesisService.getAllThesesForEmployeeByStatusName(empId, statName), Collections.emptyList());
    }


    @Test
    public void testGetAllThesesForEmployee() {
        Long empId = 1L;
        when(thesisRepository.findAllByEmployeeId(empId)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesForEmployee(empId);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesForEmployeeNotFound() {
        Long empId = 1L;
        when(thesisRepository.findAllByEmployeeId(empId)).thenReturn(Collections.emptyList());

        assertEquals(thesisService.getAllThesesForEmployee(empId), Collections.emptyList());
    }



}
