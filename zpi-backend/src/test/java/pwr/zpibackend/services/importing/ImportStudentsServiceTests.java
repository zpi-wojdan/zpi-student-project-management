package pwr.zpibackend.services.importing;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ImportStudentsServiceTests {

    @Autowired
    private ImportStudents importStudents;

    String file_path = "src/test/resources/test_plik_studenci.xlsx";

    List<ObjectNode> validData = new ArrayList<>();
    List<ObjectNode> invalidIndexData = new ArrayList<>();
    List<ObjectNode> invalidSurnameData = new ArrayList<>();
    List<ObjectNode> invalidNameData = new ArrayList<>();
    List<ObjectNode> invalidProgramData = new ArrayList<>();
    List<ObjectNode> invalidCycleData = new ArrayList<>();
    List<ObjectNode> invalidStatusData = new ArrayList<>();
    List<ObjectNode> invalidDatabaseRepetitions = new ArrayList<>();
    List<ObjectNode> invalidData = new ArrayList<>();

    public void setUp() throws IOException {
        validData.clear();
        invalidIndexData.clear();
        invalidSurnameData.clear();
        invalidNameData.clear();
        invalidProgramData.clear();
        invalidCycleData.clear();
        invalidStatusData.clear();
        invalidDatabaseRepetitions.clear();
        invalidData.clear();
        importStudents.readStudentFile(file_path, validData,
                invalidIndexData, invalidSurnameData, invalidNameData,
                invalidProgramData, invalidCycleData, invalidStatusData);
    }

    public List<ObjectNode> createSampleJson(){
        List<ObjectNode> jsonData = new ArrayList<>();

        ObjectNode entry1 = JsonNodeFactory.instance.objectNode();
        entry1.put("mail", "john.doe@pwr.edu.pl");
        entry1.put("name", "John");
        entry1.put("surname", "Doe");

        ArrayNode programsCyclesArray1 = JsonNodeFactory.instance.arrayNode();
        programsCyclesArray1.add("[[\"W04-ISTP-000P-OSIW7\",\"2020/21-Z\"]]");
        programsCyclesArray1.add("[[\"W04-INAP-000P-OSIW7\",\"2021/22-Z\"]]");
        entry1.set("programsCycles", programsCyclesArray1);

        ObjectNode entry2 = JsonNodeFactory.instance.objectNode();
        entry2.put("mail", "ava.green@pwr.edu.pl");
        entry2.put("name", "Ava");
        entry2.put("surname", "Green");

        ArrayNode programsCyclesArray2 = JsonNodeFactory.instance.arrayNode();
        programsCyclesArray2.add("[[\"W04-TELP-000P-OSIW7\",\"2022/23-Z\"]]");
        entry2.set("programsCycles", programsCyclesArray2);

        ObjectNode entry3 = JsonNodeFactory.instance.objectNode();
        entry3.put("mail", "john.doe@pwr.edu.pl");
        entry3.put("name", "John");
        entry3.put("surname", "Doe");

        ArrayNode programsCyclesArray3 = JsonNodeFactory.instance.arrayNode();
        programsCyclesArray3.add("[[\"W04-ISTP-000P-OSIW7\",\"2022/23-Z\"]]");
        programsCyclesArray3.add("[[\"W04-INAP-000P-OSIW7\",\"2022/23-Z\"]]");
        entry3.set("programsCycles", programsCyclesArray3);

        jsonData.add(entry1);
        jsonData.add(entry2);
        jsonData.add(entry3);

        return jsonData;
    }

    @Test
    public void testReadStudentFile() throws IOException {
        setUp();
        assertEquals(5, validData.size());
        assertEquals(1, invalidIndexData.size());
        assertEquals(1, invalidSurnameData.size());
        assertEquals(1, invalidNameData.size());
        assertEquals(0, invalidProgramData.size());
        assertEquals(0, invalidCycleData.size());
        assertEquals(1, invalidStatusData.size());
        assertEquals(0, invalidDatabaseRepetitions.size());
        assertEquals(0 , invalidData.size());

        ObjectNode validStudentData = validData.get(0);
        assertEquals("Zlepi", validStudentData.get("surname").asText());
        assertEquals("ProgramsCycles", validStudentData.get("name").asText());
        assertEquals("998090", validStudentData.get("index").asText());

        assertEquals("STU", validStudentData.get("status").asText());
        assertEquals("student", validStudentData.get("role").asText());
        assertEquals("998090@student.pwr.edu.pl", validStudentData.get("mail").asText());
        assertEquals("[[\"W04-INAP-000P-OSME3\",\"2022/23-Z\"]]", validStudentData.get("programsCycles").toString());
    }

    @Test
    public void testContainsProgramCycleTuple(){
        ArrayNode programsCyclesArray = JsonNodeFactory.instance.arrayNode();
        programsCyclesArray.add("[[\"W04-ISTP-000P-OSIW7\",\"2020/21-Z\"]]");
        programsCyclesArray.add("[[\"W04-TELP-000P-OSIW7\",\"2021/22-Z\"]]");
        programsCyclesArray.add("[[\"W04-INAP-000P-OSIW7\",\"2022/23-Z\"]]");
        assertTrue(importStudents.containsProgramCycleTuple(programsCyclesArray, "[[\"W04-ISTP-000P-OSIW7\",\"2020/21-Z\"]]"));
        assertFalse(importStudents.containsProgramCycleTuple(programsCyclesArray, "[[\"W04-ISTP-000P-OSIW7\",\"2023/24-Z\"]]"));
    }

    @Test
    public void testMergeRowsJson(){
        List<ObjectNode> json = createSampleJson();
        List<ObjectNode> mergedData = importStudents.mergeRowsJson(json);
        assertEquals(2, mergedData.size());

        ObjectNode mergedEntry1 = mergedData.get(0);
        ArrayNode programsCyclesArray1 = (ArrayNode) mergedEntry1.get("programsCycles");

        assertEquals("ava.green@pwr.edu.pl", mergedEntry1.get("mail").asText());
        assertEquals(1, programsCyclesArray1.size());
        assertFalse(programsCyclesArray1.toString()
                .contains("[[\"W04-ISTP-000P-OSIW7\",\"2020/21-Z\"]]"));
        assertTrue(programsCyclesArray1.toString().replaceAll("\\\\", "")
                .contains("[\"[[\"W04-TELP-000P-OSIW7\",\"2022/23-Z\"]]\"]"));

        ObjectNode mergedEntry2 = mergedData.get(1);
        ArrayNode programsCyclesArray2 = (ArrayNode) mergedEntry2.get("programsCycles");

        assertEquals("john.doe@pwr.edu.pl", mergedEntry2.get("mail").asText());
        assertEquals(4, programsCyclesArray2.size());

        assertTrue(programsCyclesArray2.toString().replaceAll("\\\\", "")
                .contains("\"[[\"W04-ISTP-000P-OSIW7\",\"2020/21-Z\"]]\""));
        assertTrue(programsCyclesArray2.toString().replaceAll("\\\\", "")
                .contains("\"[[\"W04-INAP-000P-OSIW7\",\"2022/23-Z\"]]\""));
    }

    @Test
    public void testMergeFullJson() throws IOException {
        setUp();

        Map<String, List<ObjectNode>> data = new HashMap<>();
        data.put("valid_data", validData);
        data.put("invalid_indicies", invalidIndexData);
        data.put("database_repetitions", invalidDatabaseRepetitions);
        Map<String, List<ObjectNode>> mergedData = importStudents.mergeFullJson(data);

        assertEquals(3, mergedData.size());

        List<ObjectNode> mergedValidData = mergedData.get("valid_data");
        List<ObjectNode> mergedInvalidIndicies = mergedData.get("invalid_indicies");
        List<ObjectNode> mergedDatabaseRepetitions = mergedData.get("database_repetitions");

        assertEquals(4, mergedValidData.size());
        assertEquals(1, mergedInvalidIndicies.size());
        assertEquals(0, mergedDatabaseRepetitions.size());

        boolean found222222 = false;
        boolean found000000 = false;
        for (ObjectNode mergedNode : mergedValidData) {
            if (mergedNode.get("index").asText().equals("222222")) {
                found222222 = true;
            }
            if (mergedNode.get("index").asText().equals("000000")) {
                found000000 = true;
            }
        }
        assertTrue(found222222);
        assertFalse(found000000);
    }

    @Test
    public void testDataFramesToJson() throws IOException{
        setUp();
        String result = importStudents.dataframesToJson(validData, invalidIndexData,
                invalidSurnameData, invalidNameData, invalidProgramData, invalidCycleData,
                invalidStatusData, invalidDatabaseRepetitions, invalidData);
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
                  "invalid_teaching_cycles" : [ ]
                }""";
        result = result.replaceAll("\\r\\n", "\n");
        assertEquals(expectedResult, result);
    }

    @Test
    @Transactional
    public void testSaveValidToDatabase() throws IOException {
        setUp();
        String json = importStudents.dataframesToJson(validData, invalidIndexData, invalidSurnameData,
                invalidNameData, invalidProgramData, invalidCycleData,
                invalidStatusData, invalidDatabaseRepetitions, invalidData);
        String result = importStudents.saveValidToDatabase(json);
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
                  "invalid_data" : [ {
                    "surname" : "Brakujący",
                    "name" : "Cykl",
                    "index" : "222222",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-ISTP-000P-OSIW7", "2010/11-Z" ] ],
                    "mail" : "222222@student.pwr.edu.pl"
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
                  "database_repetitions" : [ {
                    "surname" : "Zlepi",
                    "name" : "ProgramsCycles",
                    "index" : "998090",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-INAP-000P-OSME3", "2022/23-Z" ], [ "W04-ISTP-000A-OSME4", "2022/23-Z" ] ],
                    "mail" : "998090@student.pwr.edu.pl"
                  } ],
                  "valid_data" : [ {
                    "surname" : "Zlepi",
                    "name" : "ProgramsCycles",
                    "index" : "998090",
                    "status" : "STU",
                    "role" : "student",
                    "programsCycles" : [ [ "W04-INAP-000P-OSME3", "2022/23-Z" ], [ "W04-ISTP-000A-OSME4", "2022/23-Z" ] ],
                    "mail" : "998090@student.pwr.edu.pl"
                  } ],
                  "invalid_teaching_cycles" : [ ],
                  "saved_records" : 1
                }""";
        assertEquals(expectedResult, result);
    }

}
