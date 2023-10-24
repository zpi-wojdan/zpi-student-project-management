package pwr.zpibackend.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.controllers.ThesisController;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.services.EmployeeService;
import pwr.zpibackend.services.StudentService;
import pwr.zpibackend.services.ThesisService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ThesisController.class)
@AutoConfigureMockMvc(addFilters = false)
class ThesisControllerTests {

    private static final String BASE_URL = "/thesis";
    @MockBean
    private GoogleAuthService googleAuthService;
    @MockBean
    private ThesisService thesisService;
    @MockBean
    private EmployeeService employeeService;
    @MockBean
    private StudentService studentService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllTheses() throws Exception {
        List<Thesis> theses = List.of(
                new Thesis(1L, "Thesis 1 PL", "Thesis 1 EN", "Description 1", 2, new Employee("employee1@mail.com", "John", "Doe", "Role 1", "Department 1", "Title 1"), null, "Faculty 1", "Field 1", "Edu Cycle 1", "Status 1", 0),
                new Thesis(2L, "Thesis 2 PL", "Thesis 2 EN", "Description 2", 3, new Employee("employee2@mail.com", "Jane", "Smith", "Role 2", "Department 2", "Title 2"), null, "Faculty 2", "Field 2", "Edu Cycle 2", "Status 2", 1)
        );

        Mockito.when(thesisService.getAllTheses()).thenReturn(theses);

        String resultJson = """
                [
                  {
                    "id": 1,
                    "namePL": "Thesis 1 PL",
                    "nameEN": "Thesis 1 EN",
                    "description": "Description 1",
                    "num_people": 2,
                    "supervisor": {
                      "mail": "employee1@mail.com",
                      "name": "John",
                      "surname": "Doe",
                      "role": "Role 1",
                      "department_symbol": "Department 1",
                      "title": "Title 1"
                    },
                    "leader": null,
                    "faculty": "Faculty 1",
                    "field": "Field 1",
                    "edu_cycle": "Edu Cycle 1",
                    "status": "Status 1",
                    "occupied": 0
                  },
                  {
                    "id": 2,
                    "namePL": "Thesis 2 PL",
                    "nameEN": "Thesis 2 EN",
                    "description": "Description 2",
                    "num_people": 3,
                    "supervisor": {
                      "mail": "employee2@mail.com",
                      "name": "Jane",
                      "surname": "Smith",
                      "role": "Role 2",
                      "department_symbol": "Department 2",
                      "title": "Title 2"
                    },
                    "leader": null,
                    "faculty": "Faculty 2",
                    "field": "Field 2",
                    "edu_cycle": "Edu Cycle 2",
                    "status": "Status 2",
                    "occupied": 1
                  }
                ]
                """;

        mockMvc.perform(get(BASE_URL).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getAllTheses();
    }

    @Test
    public void testGetThesisById() throws Exception {
        Thesis thesis = new Thesis(1L, "Thesis 1 PL", "Thesis 1 EN", "Description 1", 2, new Employee("employee1@mail.com", "John", "Doe", "Role 1", "Department 1", "Title 1"), null, "Faculty 1", "Field 1", "Edu Cycle 1", "Status 1", 0);
        Long thesisId = 1L;

        Mockito.when(thesisService.getThesis(thesisId)).thenReturn(thesis);

        String resultJson = """
                  {
                    "id": 1,
                    "namePL": "Thesis 1 PL",
                    "nameEN": "Thesis 1 EN",
                    "description": "Description 1",
                    "num_people": 2,
                    "supervisor": {
                      "mail": "employee1@mail.com",
                      "name": "John",
                      "surname": "Doe",
                      "role": "Role 1",
                      "department_symbol": "Department 1",
                      "title": "Title 1"
                    },
                    "leader": null,
                    "faculty": "Faculty 1",
                    "field": "Field 1",
                    "edu_cycle": "Edu Cycle 1",
                    "status": "Status 1",
                    "occupied": 0
                  }
                """;

        mockMvc.perform(get(BASE_URL + "/{id}", thesisId).contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(thesisService).getThesis(thesisId);
    }

    @Test
    public void testGetThesisByIdNotFound() throws Exception {
        Long thesisId = 1L;

        Mockito.when(thesisService.getThesis(thesisId)).thenThrow(new NotFoundException());

        mockMvc.perform(get(BASE_URL + "/{id}", thesisId).contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(thesisService).getThesis(thesisId);
    }
}