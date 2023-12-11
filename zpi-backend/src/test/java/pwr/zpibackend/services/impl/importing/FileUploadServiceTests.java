package pwr.zpibackend.services.impl.importing;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import pwr.zpibackend.dto.user.EmployeeDTO;
import pwr.zpibackend.models.university.*;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Role;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.repositories.university.*;
import pwr.zpibackend.repositories.user.EmployeeRepository;
import pwr.zpibackend.repositories.user.RoleRepository;
import pwr.zpibackend.repositories.user.StudentRepository;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FileUploadServiceTests {
    @InjectMocks
    private ImportEmployees importEmployees;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private TitleRepository titleRepository;
    @Mock
    private FacultyRepository facultyRepository;
    @InjectMocks
    private ImportStudents importStudents;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private StudyCycleRepository cycleRepository;
    String file_path_stud = "src/test/resources/test_plik_studenci.xlsx";
    String file_path_emp = "src/test/resources/test_plik_pracownicy.xlsx";

    @Test
    public void testProcessStudentFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "originalFileName.txt",
                "text/plain",
                "content".getBytes()
        );
        MockMultipartFile spyFile = Mockito.spy(file);
        Mockito.when(spyFile.isEmpty()).thenReturn(false);

        Program program = new Program();
        StudyCycle cycle = new StudyCycle();
        Student student = new Student();
        Role role = new Role();

        Mockito.when(programRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.of(program));
        Mockito.when(cycleRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.of(cycle));
        Mockito.when(roleRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.of(role));
        Mockito.when(studentRepository.save(ArgumentMatchers.any())).thenReturn(student);

        String result = importStudents.processFile(file_path_stud);
        result = result.replaceAll("\\r\\n", "\n");

        String expectedResult = """
                {
                  "invalid_names" : [ {
                    "surname" : "Złeimie",
                    "name" : "?",
                    "index" : "444444",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-ISTP-000P-OSIW7", "2020/21-Z" ] ],
                    "mail" : "444444@student.pwr.edu.pl"
                  } ],
                  "invalid_programs" : [ ],
                  "invalid_statuses" : [ {
                    "surname" : "Zły",
                    "name" : "Status",
                    "index" : "123456",
                    "status" : "STUUUU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-ISTP-000P-OQEW1", "2020/21-Z" ] ],
                    "mail" : "123456@student.pwr.edu.pl"
                  } ],
                  "invalid_indices" : [ {
                    "surname" : "Zły",
                    "name" : "Indeks",
                    "index" : "1234567",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-ISTP-000P-OSIW7", "2023/24-Z" ] ],
                    "mail" : "1234567@student.pwr.edu.pl"
                  } ],
                  "invalid_surnames" : [ {
                    "surname" : "?",
                    "name" : "Złenazwisko",
                    "index" : "333333",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-ISTP-000P-OSIW7", "2020/21-Z" ] ],
                    "mail" : "333333@student.pwr.edu.pl"
                  } ],
                  "invalid_data" : [ ],
                  "database_repetitions" : [ ],
                  "valid_data" : [ {
                    "surname" : "Brakujący",
                    "name" : "Cykl",
                    "index" : "222222",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-ISTP-000P-OSIW7", "2010/11-Z" ] ],
                    "mail" : "222222@student.pwr.edu.pl"
                  }, {
                    "surname" : "Zlepi",
                    "name" : "ProgramsCycles",
                    "index" : "998090",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-INAP-000P-OSME3", "2022/23-Z" ], [ "W04-ISTP-000A-OSME4", "2022/23-Z" ] ],
                    "mail" : "998090@student.pwr.edu.pl"
                  }, {
                    "surname" : "Brakujące",
                    "name" : "ProgramsCycles",
                    "index" : "999999",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-ISTP-000P-QQQQ1", "2010/11-Z" ] ],
                    "mail" : "999999@student.pwr.edu.pl"
                  }, {
                    "surname" : "Brakujący",
                    "name" : "Program",
                    "index" : "111111",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-XDDP-000P-OSIW7", "2020/21-Z" ] ],
                    "mail" : "111111@student.pwr.edu.pl"
                  } ],
                  "invalid_teaching_cycles" : [ ],
                  "saved_records" : 4
                }""";
        assertEquals(expectedResult, result);
    }

    @Test
    public void testProcessEmployeeFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "originalFileName.txt",
                "text/plain",
                "content".getBytes()
        );
        MockMultipartFile spyFile = Mockito.spy(file);
        Mockito.when(spyFile.isEmpty()).thenReturn(false);

        Employee emp = new Employee();
        EmployeeDTO empDTO = new EmployeeDTO();
        Role role = new Role();
        Department department = new Department();
        Title title = new Title();
        Faculty faculty = new Faculty();
        when(employeeRepository.save(ArgumentMatchers.any())).thenReturn(emp);
        when(employeeRepository.findByMail("test.test@pwr.edu.pl")).thenReturn(Optional.of(emp));
        when(roleRepository.findByName("employee")).thenReturn(Optional.of(role));
        when(departmentRepository.findByCode("K30W04ND03")).thenReturn(Optional.of(department));
        when(titleRepository.findByName("dr")).thenReturn(Optional.of(title));
        when(facultyRepository.findByAbbreviation("W04N")).thenReturn(Optional.of(faculty));

        String result = importEmployees.processFile(file_path_emp);
        result = result.replaceAll("\\r\\n", "\n");
        String expectedResult = """
                {
                  "valid_data" : [ {
                    "id" : "3",
                    "title" : "dr",
                    "surname" : "Będzie",
                    "name" : "Powtórka",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "tomasz.babczynski@pwr.edu.pl",
                    "role" : "supervisor"
                  }, {
                    "id" : "3",
                    "title" : "dr",
                    "surname" : "Będzie",
                    "name" : "Powtórka",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "tomasz.babczynski@pwr.edu.pl",
                    "role" : "supervisor"
                  }, {
                    "id" : "239",
                    "title" : "dr",
                    "surname" : "Git",
                    "name" : "Git",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "123456789",
                    "email" : "tomasz.babczynskiii@pwr.edu.pl",
                    "role" : "supervisor"
                  }, {
                    "id" : "240",
                    "title" : "dr",
                    "surname" : "Git",
                    "name" : "Git",
                    "faculty" : "W04N",
                    "department" : "K30W04XD10",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "+48234567890",
                    "email" : "tomasz.babczynskiiii@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "invalid_indices" : [ {
                    "id" : "?",
                    "title" : "dr",
                    "surname" : "Złe",
                    "name" : "Id",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "zle.id@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "invalid_academic_titles" : [ {
                    "id" : "243",
                    "title" : "?",
                    "surname" : "Zły",
                    "name" : "Tytuł",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "zly.tytul@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "invalid_surnames" : [ {
                    "id" : "244",
                    "title" : "dr",
                    "surname" : "?",
                    "name" : "Złenazwisko",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "zle.nazwisko@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "invalid_names" : [ {
                    "id" : "245",
                    "title" : "dr",
                    "surname" : "Złeimie",
                    "name" : "?",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "zle.imie@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "invalid_units" : [ {
                    "id" : "246",
                    "title" : "dr",
                    "surname" : "Zły",
                    "name" : "Wydział",
                    "faculty" : "?",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "zly.wydzial@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "invalid_subunits" : [ {
                    "id" : "247",
                    "title" : "dr",
                    "surname" : "Zła",
                    "name" : "Katedra",
                    "faculty" : "W04N",
                    "department" : "?",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "zla.katedra@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "invalid_positions" : [ {
                    "id" : "248",
                    "title" : "dr",
                    "surname" : "Złe",
                    "name" : "Stanowisko",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "?",
                    "phoneNumber" : "",
                    "email" : "zle.stanowisko@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "invalid_phone_numbers" : [ {
                    "id" : "249",
                    "title" : "dr",
                    "surname" : "Zły",
                    "name" : "Telefon",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "?",
                    "email" : "zly.telefon@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "invalid_emails" : [ {
                    "id" : "241",
                    "title" : "dr",
                    "surname" : "Zły",
                    "name" : "Email",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "xxx.ddd@gmail.com",
                    "role" : "supervisor"
                  } ],
                  "database_repetitions" : [ ],
                  "invalid_data" : [ {
                    "id" : "3",
                    "title" : "dr",
                    "surname" : "Będzie",
                    "name" : "Powtórka",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "tomasz.babczynski@pwr.edu.pl",
                    "role" : "supervisor"
                  }, {
                    "id" : "3",
                    "title" : "dr",
                    "surname" : "Będzie",
                    "name" : "Powtórka",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "",
                    "email" : "tomasz.babczynski@pwr.edu.pl",
                    "role" : "supervisor"
                  }, {
                    "id" : "239",
                    "title" : "dr",
                    "surname" : "Git",
                    "name" : "Git",
                    "faculty" : "W04N",
                    "department" : "K30W04ND03",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "123456789",
                    "email" : "tomasz.babczynskiii@pwr.edu.pl",
                    "role" : "supervisor"
                  }, {
                    "id" : "240",
                    "title" : "dr",
                    "surname" : "Git",
                    "name" : "Git",
                    "faculty" : "W04N",
                    "department" : "K30W04XD10",
                    "position" : "Adiunkt (N1-0501)",
                    "phoneNumber" : "+48234567890",
                    "email" : "tomasz.babczynskiiii@pwr.edu.pl",
                    "role" : "supervisor"
                  } ],
                  "saved_records" : 0
                }""";
        assertEquals(expectedResult, result);
    }

}
