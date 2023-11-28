package pwr.zpibackend.services.thesis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
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
    private List<Thesis> publicTheses;
    private List<Status> statuses;

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
        thesis.setSupervisor(emp);
        thesis.setPrograms(List.of(new Program()));
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
        theses.add(thesis);
        theses.add(thesis2);
        theses.add(thesis3);
        theses.add(thesis4);
        theses.add(thesis5);
        theses.add(thesis6);

        publicTheses = new ArrayList<>();
        publicTheses.add(thesis6);
        publicTheses.add(thesis5);
        publicTheses.add(thesis4);
    }

    @Test
    public void testGetAllTheses() {

        when(thesisRepository.findAll()).thenReturn(theses);

        List<Thesis> result = thesisService.getAllTheses();

        assertEquals(6, result.size());
        assertEquals(theses, result);
    }

    @Test
    public void testGetAllPublicTheses() {
        List <String> statusNames = List.of("Approved", "Assigned", "Closed");
        Sort sort = Sort.by(Sort.Direction.DESC, "studyCycle.name", "id");
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
        Long statusId = 1L;
        when(thesisRepository.findAllByStatusId(statusId)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesByStatusId(statusId);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusId() {
        Long empId = 1L;
        Long statId = 1L;
        when(thesisRepository.findAllBySupervisorIdAndStatusId(empId, statId)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesForEmployeeByStatusId(empId, statId);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusIdNotFound() {
        Long empId = 1L;
        Long statId = 1L;
        when(thesisRepository.findAllBySupervisorIdAndStatusId(empId, statId)).thenReturn(Collections.emptyList());

        assertEquals(thesisService.getAllThesesForEmployeeByStatusId(empId, statId), Collections.emptyList());
    }


    @Test
    public void testGetAllThesesForEmployee() {
        Long empId = 1L;
        when(thesisRepository.findAllBySupervisorId(empId)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesForEmployee(empId);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesForEmployeeNotFound() {
        Long empId = 1L;
        when(thesisRepository.findAllBySupervisorId(empId)).thenReturn(Collections.emptyList());

        assertEquals(thesisService.getAllThesesForEmployee(empId), Collections.emptyList());
    }



}
