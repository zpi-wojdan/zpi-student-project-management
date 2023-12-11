package pwr.zpibackend.services.impl.importing;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudyCycle;
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

//    @Test
//    public void testProcessStudentFileFailure() {
//        MockMultipartFile file = new MockMultipartFile(
//                "file",
//                "originalFileName.txt",
//                "text/plain",
//                new byte[0]
//        );
//        when(file.isEmpty()).thenReturn(true);
//        try{
//            String result = importStudents.processFile(file_path_stud);
//        }
//        catch (Exception e){
//            assertInstanceOf(IOException.class, e);
//        }
//    }

    @Test
    public void testProcessStudentFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "originalFileName.txt",
                "text/plain",
                "content".getBytes()
        );
        MockMultipartFile spyFile = Mockito.spy(file);

        when(spyFile.isEmpty()).thenReturn(false);

        Program program = new Program();
        StudyCycle cycle = new StudyCycle();
        Student student = new Student();
        Role role = new Role();

        when(programRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.of(program));
        when(cycleRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.of(cycle));
        when(roleRepository.findByName(ArgumentMatchers.any())).thenReturn(Optional.of(role));
        when(studentRepository.save(ArgumentMatchers.any())).thenReturn(student);

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

//    @Test
//    public void testProcessEmployeeFile() throws Exception {
//        importEmployees.processFile(file_path_emp);
//    }

}
