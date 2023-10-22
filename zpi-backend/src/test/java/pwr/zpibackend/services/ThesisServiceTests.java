package pwr.zpibackend.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.repositories.ThesisRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ThesisServiceTests {

    @Mock
    private ThesisRepository thesisRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ThesisService thesisService;

    @Test
    public void testGetAllTheses() {
        List<Thesis> theses = List.of(
                new Thesis(1L, "Thesis 1 PL", "Thesis 1 EN", "Description 1", 2, new Employee("employee1@mail.com", "John", "Doe", "Role 1", "Department 1", "Title 1"), "Faculty 1", "Field 1", "Edu Cycle 1", "Status 1", 0),
                new Thesis(2L, "Thesis 2 PL", "Thesis 2 EN", "Description 2", 3, new Employee("employee2@mail.com", "Jane", "Smith", "Role 2", "Department 2", "Title 2"), "Faculty 2", "Field 2", "Edu Cycle 2", "Status 2", 1)
        );

        when(thesisRepository.findAll()).thenReturn(theses);

        List<Thesis> result = thesisService.getAllTheses();

        assertEquals(theses, result);
    }

    @Test
    public void testGetThesisById() throws NotFoundException {
        Long thesisId = 1L;
        Thesis thesis = new Thesis(1L, "Thesis 1 PL", "Thesis 1 EN", "Description 1", 2, new Employee("employee1@mail.com", "John", "Doe", "Role 1", "Department 1", "Title 1"), "Faculty 1", "Field 1", "Edu Cycle 1", "Status 1", 0);

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
