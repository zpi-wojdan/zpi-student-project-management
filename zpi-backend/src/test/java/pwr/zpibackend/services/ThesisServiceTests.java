package pwr.zpibackend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.repositories.ThesisRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ThesisServiceTests {

    @Mock
    private ThesisRepository thesisRepository;

    @Mock
    private EmployeeRepository employeeRepository;

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
        thesis.setDescription("Description 1");
        thesis.setNum_people(4);
        thesis.setSupervisor(mock(Employee.class));
        thesis.setPrograms(List.of(mock(Program.class)));

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
}
