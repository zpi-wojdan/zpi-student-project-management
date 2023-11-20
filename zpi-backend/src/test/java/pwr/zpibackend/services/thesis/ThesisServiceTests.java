package pwr.zpibackend.services.thesis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.thesis.Comment;
import pwr.zpibackend.models.thesis.Status;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.repositories.thesis.CommentRepository;
import pwr.zpibackend.repositories.thesis.StatusRepository;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.thesis.ThesisRepository;
import pwr.zpibackend.services.thesis.ThesisService;

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
        thesis.setStatus(new Status(1, "Draft"));

        theses = new ArrayList<>();
        theses.add(thesis);
    }

    @Test
    public void testGetAllTheses() {

        when(thesisRepository.findAll()).thenReturn(theses);

        List<Thesis> result = thesisService.getAllTheses();

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
        Thesis thesisToAdd = new Thesis();
        Employee supervisor = new Employee();
        supervisor.setId(1L);

        when(employeeRepository.findById(supervisor.getId())).thenReturn(Optional.of(supervisor));
        when(thesisRepository.saveAndFlush(any(Thesis.class))).thenAnswer(invocation -> {
            Thesis savedThesis = invocation.getArgument(0);
            savedThesis.setId(1L);
            return savedThesis;
        });

        thesisToAdd.setNamePL("Thesis 1 PL");
        thesisToAdd.setNameEN("Thesis 1 EN");
        thesisToAdd.setDescriptionPL("Description 1");
        thesisToAdd.setDescriptionEN("Description 1");
        thesisToAdd.setNumPeople(4);

        thesis.setPrograms(List.of(new Program()));
        thesis.setStatus(new Status(1, "Draft"));
        thesisToAdd.setSupervisor(supervisor);

        Thesis result = thesisService.addThesis(thesisToAdd);

        verify(thesisRepository).saveAndFlush(any(Thesis.class));

        assertNotNull(result.getId());
        assertEquals(thesisToAdd, result);
    }



    @Test
    public void testUpdateThesis() throws NotFoundException {
        Long thesisId = 1L;
        Thesis thesisToUpdate = new Thesis();
        Employee supervisor = new Employee();
        supervisor.setId(1L);

        when(employeeRepository.existsById(supervisor.getId())).thenReturn(true);
        when(employeeRepository.findById(supervisor.getId())).thenReturn(Optional.of(supervisor));
        when(thesisRepository.existsById(thesisId)).thenReturn(true);
        when(thesisRepository.findById(thesisId)).thenReturn(Optional.of(thesis));
        when(thesisRepository.saveAndFlush(any(Thesis.class))).thenAnswer(invocation -> {
            Thesis savedThesis = invocation.getArgument(0);
            savedThesis.setId(thesisId);
            savedThesis.setNamePL("XD");
            return savedThesis;
        });

        thesisToUpdate.setNamePL("Thesis 1 PL");
        thesisToUpdate.setNameEN("Thesis 1 EN");
        thesisToUpdate.setDescriptionPL("Description 1");
        thesisToUpdate.setDescriptionEN("Description 1");
        thesisToUpdate.setNumPeople(4);
        thesisToUpdate.setSupervisor(supervisor);

        thesis.setPrograms(List.of(new Program()));
        thesis.setStatus(new Status(1, "Draft"));

        Thesis result = thesisService.updateThesis(thesisId, thesisToUpdate);
        System.out.println(result.getNamePL());
        assertEquals(thesis, result);
    }

    @Test
    public void testUpdateThesisNotFound() {
        Long thesisId = 1L;
        Thesis thesisToUpdate = mock(Thesis.class);
        when(thesisRepository.findById(thesisId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> thesisService.updateThesis(thesisId, thesisToUpdate));
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
    public void testGetAllThesesExcludingStatusId() throws NotFoundException {
        Long statusId = 1L;
        when(thesisRepository.findAll()).thenReturn(new ArrayList<>());
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(new Status(statusId, "Draft")));

        List<Thesis> result = thesisService.getAllThesesExcludingStatusId(statusId);
        verify(statusRepository).findById(statusId);
        verify(thesisRepository).findAll();

        List<Thesis> expectedResult = theses.stream()
                .filter(thesis -> !statusId.equals(thesis.getStatus().getId()))
                .collect(Collectors.toList());
        assertEquals(expectedResult, new ArrayList<>());
    }

    @Test
    public void testGetAllThesesExcludingStatusIdNotFound() {
        Long statusId = 1L;
        when(statusRepository.findById(statusId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> thesisService.getAllThesesExcludingStatusId(statusId));
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusId() {
        Long empId = 1L;
        Long statId = 1L;
        when(thesisRepository.findAllByEmployeeIdAndStatusName(empId, statId)).thenReturn(theses);

        List<Thesis> result = thesisService.getAllThesesForEmployeeByStatusId(empId, statId);

        assertEquals(theses, result);
    }

    @Test
    public void testGetAllThesesForEmployeeByStatusIdNotFound() {
        Long empId = 1L;
        Long statId = 1L;
        when(thesisRepository.findAllByEmployeeIdAndStatusName(empId, statId)).thenReturn(Collections.emptyList());

        assertEquals(thesisService.getAllThesesForEmployeeByStatusId(empId, statId), Collections.emptyList());
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
