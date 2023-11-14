package pwr.zpibackend.services.importing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ImportEmployeesServiceTests {

    @Autowired
    private ImportEmployees importEmployees;

    String file_path = "src/test/resources/test_plik_pracownicy.xlsx";

    List<ObjectNode> validData = new ArrayList<>();
    List<ObjectNode> invalidIndexData = new ArrayList<>();
    List<ObjectNode> invalidAcademicTitleData = new ArrayList<>();
    List<ObjectNode> invalidSurnameData = new ArrayList<>();
    List<ObjectNode> invalidNameData = new ArrayList<>();
    List<ObjectNode> invalidUnitData = new ArrayList<>();
    List<ObjectNode> invalidSubunitData = new ArrayList<>();
    List<ObjectNode> invalidPositionsData = new ArrayList<>();
    List<ObjectNode> invalidPhoneNumberData = new ArrayList<>();
    List<ObjectNode> invalidEmailData = new ArrayList<>();
    List<ObjectNode> invalidDatabaseRepetitions = new ArrayList<>();
    List<ObjectNode> invalidData = new ArrayList<>();

    public void setUp() throws IOException{
        validData.clear();
        invalidIndexData.clear();
        invalidAcademicTitleData.clear();
        invalidSurnameData.clear();
        invalidNameData.clear();
        invalidUnitData.clear();
        invalidSubunitData.clear();
        invalidPositionsData.clear();
        invalidPhoneNumberData.clear();
        invalidEmailData.clear();
        invalidDatabaseRepetitions.clear();
        invalidData.clear();
        importEmployees.readEmployeeFile(file_path, validData,
                invalidIndexData, invalidAcademicTitleData, invalidSurnameData,
                invalidNameData, invalidUnitData, invalidSubunitData,
                invalidPositionsData, invalidPhoneNumberData, invalidEmailData);
    }

    @Test
    public void testReadEmployeeFile() throws IOException {
        setUp();
        assertEquals(4, validData.size());
        assertEquals(1, invalidIndexData.size());
        assertEquals(1, invalidAcademicTitleData.size());
        assertEquals(1, invalidSurnameData.size());
        assertEquals(1, invalidNameData.size());
        assertEquals(1, invalidUnitData.size());
        assertEquals(1, invalidSubunitData.size());
        assertEquals(1, invalidPositionsData.size());
        assertEquals(1, invalidPhoneNumberData.size());
        assertEquals(1, invalidEmailData.size());
        assertEquals(0, invalidDatabaseRepetitions.size());
        assertEquals(0, invalidData.size());

        ObjectNode validEmployeeData = validData.get(0);

        assertEquals("3", validEmployeeData.get("id").asText());
        assertEquals("dr", validEmployeeData.get("title").asText());
        assertEquals("Będzie", validEmployeeData.get("surname").asText());
        assertEquals("Powtórka", validEmployeeData.get("name").asText());
        assertEquals("W04N", validEmployeeData.get("faculty").asText());
        assertEquals("K30W04ND03", validEmployeeData.get("department").asText());
        assertEquals("Adiunkt (N1-0501)", validEmployeeData.get("position").asText());
        assertEquals("", validEmployeeData.get("phoneNumber").asText());
        assertEquals("tomasz.babczynski@pwr.edu.pl", validEmployeeData.get("email").asText());
        assertEquals("supervisor", validEmployeeData.get("role").asText());
    }

    @Test
    @Transactional
    public void testSaveValidToDatabase() throws IOException {
        setUp();
        int savedRecords = importEmployees.saveValidToDatabase(validData, invalidIndexData,
                invalidAcademicTitleData, invalidSurnameData, invalidNameData, invalidUnitData,
                invalidSubunitData, invalidPositionsData, invalidPhoneNumberData, invalidEmailData,
                invalidDatabaseRepetitions, invalidData);
        assertEquals(0, savedRecords);
    }

    @Test
    @Transactional
    public void testDataframesToJson() throws IOException {
        setUp();
        int savedRecords = importEmployees.saveValidToDatabase(validData, invalidIndexData,
                invalidAcademicTitleData, invalidSurnameData, invalidNameData, invalidUnitData,
                invalidSubunitData, invalidPositionsData, invalidPhoneNumberData, invalidEmailData,
                invalidDatabaseRepetitions, invalidData);
        String result = importEmployees.dataframesToJson(validData, invalidIndexData,
                invalidAcademicTitleData, invalidSurnameData, invalidNameData, invalidUnitData,
                invalidSubunitData, invalidPositionsData, invalidPhoneNumberData, invalidEmailData,
                invalidDatabaseRepetitions, invalidData, savedRecords);
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
                    "faculty" : "W10",
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
                  "database_repetitions" : [ {
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
                  } ],
                  "invalid_data" : [ {
                    "id" : "239",
                    "title" : "dr",
                    "surname" : "Git",
                    "name" : "Git",
                    "faculty" : "W10",
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
