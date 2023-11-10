package pwr.zpibackend.services.importing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ImportStudentsServiceTests {

    @Autowired
    private ImportStudents importStudents;

    @Test
    public void testReadStudentFile() throws IOException {
        String file_path = "src/test/resources/poprawny_plik_studenci.xlsx";

        List<ObjectNode> validData = new ArrayList<>();
        List<ObjectNode> invalidIndexData = new ArrayList<>();
        List<ObjectNode> invalidSurnameData = new ArrayList<>();
        List<ObjectNode> invalidNameData = new ArrayList<>();
        List<ObjectNode> invalidProgramData = new ArrayList<>();
        List<ObjectNode> invalidCycleData = new ArrayList<>();
        List<ObjectNode> invalidStatusData = new ArrayList<>();

        importStudents.readStudentFile(file_path, validData, invalidIndexData, invalidSurnameData, invalidNameData, invalidProgramData, invalidCycleData, invalidStatusData);

        assertEquals(52, validData.size());
        assertEquals(0, invalidIndexData.size());
        assertEquals(0, invalidSurnameData.size());
        assertEquals(0, invalidNameData.size());
        assertEquals(0, invalidProgramData.size());
        assertEquals(0, invalidCycleData.size());
        assertEquals(0, invalidStatusData.size());

        ObjectNode validStudentData = validData.get(0);
        assertEquals("Doe", validStudentData.get("surname").asText());
        assertEquals("John", validStudentData.get("name").asText());
        assertEquals("123456", validStudentData.get("index").asText());

        assertEquals("STU", validStudentData.get("status").asText());
        assertEquals("student", validStudentData.get("role").asText());
        assertEquals("123456@student.pwr.edu.pl", validStudentData.get("mail").asText());
        assertEquals("[[\"W04-ISTP-000P-OSIW7\",\"2020/21-Z\"]]", validStudentData.get("programsCycles").toString());
    }


    @Test
    public void testSaveV

}
