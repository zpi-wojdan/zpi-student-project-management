package pwr.zpibackend.services.importing;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ImportUtilsTests {

    @Test
    public void testEmailRegex(){
        assertTrue(ImportUtils.isValidEmail("jane.smith@pwr.edu.pl"));
        assertTrue(ImportUtils.isValidEmail("alice.student@pwr.edu.pl"));
        assertTrue(ImportUtils.isValidEmail("bob.student@student.pwr.edu.pl"));
        assertTrue(ImportUtils.isValidEmail("peter.pwr.wroc.pl@pwr.wroc.pl"));

        assertFalse(ImportUtils.isValidEmail("invalid_email"));
        assertFalse(ImportUtils.isValidEmail("whatever@pwr.com"));
        assertFalse(ImportUtils.isValidEmail("test.email@invalid-domain.com"));
        assertFalse(ImportUtils.isValidEmail("user.name@pwr.wroc.edu.pl.unnecessary.part"));
    }

    @Test
    public void testPhoneRegex(){
        assertTrue(ImportUtils.isValidPhoneNumber("123456789"));
        assertTrue(ImportUtils.isValidPhoneNumber("+48123456789"));
        assertTrue(ImportUtils.isValidPhoneNumber("+48 123 456 789"));
        assertTrue(ImportUtils.isValidPhoneNumber("+48 123-456-789"));
        assertTrue(ImportUtils.isValidPhoneNumber("+48 123456789"));
        assertTrue(ImportUtils.isValidPhoneNumber(""));

        assertFalse(ImportUtils.isValidPhoneNumber("12345678901234567890A"));
        assertFalse(ImportUtils.isValidPhoneNumber("12345678901234567890!"));
    }

    @Test
    public void testPositionRegex(){
        assertTrue(ImportUtils.isValidPosition("student"));
        assertTrue(ImportUtils.isValidPosition("leader"));
        assertTrue(ImportUtils.isValidPosition("admin"));
        assertTrue(ImportUtils.isValidPosition("employee"));
        assertTrue(ImportUtils.isValidPosition("12345678901234567890A"));
        assertTrue(ImportUtils.isValidPosition("àáâäãåčćèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçč"));
        assertTrue(ImportUtils.isValidPosition("šžÀÁÂÄÃÅĆČĖÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙ"));
        assertTrue(ImportUtils.isValidPosition("ÚÛÜŲŪŸÝŻŹÑ ßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,."));
        assertTrue(ImportUtils.isValidPosition(""));


        assertFalse(ImportUtils.isValidPosition("12345678901234567890!"));
        assertFalse(ImportUtils.isValidPosition("12345678901234567890A12345678901234567890A12345678901234567890A12345678901234567890A"));
    }

    @Test
    public void testSubunitRegex(){
        assertTrue(ImportUtils.isValidSubunit("W4N/KP"));
        assertTrue(ImportUtils.isValidSubunit("W4N/KP/"));
        assertTrue(ImportUtils.isValidSubunit("W4N/KP/1"));
        assertTrue(ImportUtils.isValidSubunit("W4N/KP/1/"));
        assertTrue(ImportUtils.isValidSubunit("W4N/KP/1/2"));

        assertFalse(ImportUtils.isValidSubunit("abcdef!"));
        assertFalse(ImportUtils.isValidSubunit("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        assertFalse(ImportUtils.isValidSubunit(""));
    }

    @Test
    public void testUnitRegex(){
        assertTrue(ImportUtils.isValidUnit("W4N"));
        assertTrue(ImportUtils.isValidUnit("W04N"));
        assertTrue(ImportUtils.isValidUnit("W10"));

        assertFalse(ImportUtils.isValidUnit("abcdef"));
        assertFalse(ImportUtils.isValidUnit("ABCDEF"));
        assertFalse(ImportUtils.isValidUnit("ABC!"));
        assertFalse(ImportUtils.isValidUnit("AB"));
        assertFalse(ImportUtils.isValidUnit(""));
    }

    @Test
    public void testStatusRegex(){
        assertTrue(ImportUtils.isValidStatus("A"));
        assertTrue(ImportUtils.isValidStatus("ABC"));
        assertTrue(ImportUtils.isValidStatus("ABCDE"));

        assertFalse(ImportUtils.isValidStatus("abcde"));
        assertFalse(ImportUtils.isValidStatus("ABC!"));
        assertFalse(ImportUtils.isValidStatus("AB1"));
        assertFalse(ImportUtils.isValidStatus(""));
    }

    @Test
    public void testTeachingCycleRegex(){
        assertTrue(ImportUtils.isValidTeachingCycle("2020/21-A"));
        assertTrue(ImportUtils.isValidTeachingCycle("2020/21-AAA"));

        assertFalse(ImportUtils.isValidTeachingCycle("2020/21-AAAA"));
        assertFalse(ImportUtils.isValidTeachingCycle("2020/21-"));
        assertFalse(ImportUtils.isValidTeachingCycle("2020/21"));
        assertFalse(ImportUtils.isValidTeachingCycle("2020/21-1"));
        assertFalse(ImportUtils.isValidTeachingCycle("2020/21-a"));
        assertFalse(ImportUtils.isValidTeachingCycle("2020/21-A-"));
        assertFalse(ImportUtils.isValidTeachingCycle("2020-21-A"));
        assertFalse(ImportUtils.isValidTeachingCycle("2020/2021-A"));
        assertFalse(ImportUtils.isValidTeachingCycle("2020/2021-!"));
        assertFalse(ImportUtils.isValidTeachingCycle(""));
    }

    @Test
    public void testOrdinalNumberRegex(){
        assertTrue(ImportUtils.isValidOrdinalNumber("1"));
        assertTrue(ImportUtils.isValidOrdinalNumber("12345"));

        assertFalse(ImportUtils.isValidOrdinalNumber("123456"));
        assertFalse(ImportUtils.isValidOrdinalNumber("12345A"));
        assertFalse(ImportUtils.isValidOrdinalNumber("12345a"));
        assertFalse(ImportUtils.isValidOrdinalNumber("12345!"));
        assertFalse(ImportUtils.isValidOrdinalNumber(""));
    }

    @Test
    public void testNameRegex(){
        assertTrue(ImportUtils.isValidName("John"));
        assertTrue(ImportUtils.isValidName("John-John"));
        assertTrue(ImportUtils.isValidName("àáâäãåčćèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçč"));
        assertTrue(ImportUtils.isValidName("šžÀÁÂÄÃÅĆČĖÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙ"));
        assertTrue(ImportUtils.isValidName("ÚÛÜŲŪŸÝŻŹÑ ßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,."));

        assertFalse(ImportUtils.isValidName(""));
        assertFalse(ImportUtils.isValidName("John123"));
        assertFalse(ImportUtils.isValidName("John!"));
        assertFalse(ImportUtils.isValidName("John@"));
        assertFalse(ImportUtils.isValidName("JohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohn"));
    }

    @Test
    public void testSurnameRegex(){
        assertTrue(ImportUtils.isValidSurname("Kowalski"));
        assertTrue(ImportUtils.isValidSurname("Kowalski-Kowalski"));
        assertTrue(ImportUtils.isValidSurname("àáâäãåčćèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçč"));
        assertTrue(ImportUtils.isValidSurname("šžÀÁÂÄÃÅĆČĖÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙ"));
        assertTrue(ImportUtils.isValidSurname("ÚÛÜŲŪŸÝŻŹÑ ßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,."));

        assertFalse(ImportUtils.isValidSurname(""));
        assertFalse(ImportUtils.isValidSurname("Kowalski123"));
        assertFalse(ImportUtils.isValidSurname("Kowalski!"));
        assertFalse(ImportUtils.isValidSurname("Kowalski@"));
        assertFalse(ImportUtils.isValidSurname("KowalskiKowalskiKowalskiKowalskiKowalskiKowalskiKowalskiKowalskiKowalskiKowalskiKowalskiKowalskiKowalskiKowalskiKowalski"));
    }

    @Test
    public void testIndexRegex(){
        assertTrue(ImportUtils.isValidIndex("123456"));

        assertFalse(ImportUtils.isValidIndex("12345"));
        assertFalse(ImportUtils.isValidIndex("1234567"));
        assertFalse(ImportUtils.isValidIndex("12345A"));
        assertFalse(ImportUtils.isValidIndex("12345!"));
        assertFalse(ImportUtils.isValidIndex("012345"));
        assertFalse(ImportUtils.isValidIndex(""));
    }

    @Test
    public void testCreatingStudentMail(){
        assertEquals( "123456@student.pwr.edu.pl", ImportUtils.createStudentMail("123456"));
        assertEquals("", ImportUtils.createStudentMail(""));

        assertNotEquals("123456", ImportUtils.createStudentMail("123456"));
        assertNotEquals("", ImportUtils.createStudentMail("123456"));
        assertNotEquals(null, ImportUtils.createStudentMail(""));
    }

    private Cell createMockCell(CellType cellType, Object value, String dataFormatString) {
        Cell cell = mock(Cell.class);
        when(cell.getCellType()).thenReturn(cellType);

        if (cellType == CellType.STRING) {
            when(cell.getStringCellValue()).thenReturn(String.valueOf(value));
        } else if (cellType == CellType.NUMERIC) {
            when(cell.getNumericCellValue()).thenReturn(Double.parseDouble(String.valueOf(value)));
            CellStyle cellStyle = mock(CellStyle.class);
            when(cellStyle.getDataFormatString()).thenReturn(dataFormatString);
            when(cell.getCellStyle()).thenReturn(cellStyle);
        } else if (cellType == CellType.BOOLEAN) {
            when(cell.getBooleanCellValue()).thenReturn((boolean) value);
        }
        return cell;
    }

    @Test
    public void testCellToObject(){
        Cell stringCell = createMockCell(CellType.STRING, "kowalski", null);
        Cell numericCell = createMockCell(CellType.NUMERIC, 420, null);
        Cell percentageCell = createMockCell(CellType.NUMERIC, 0.07, "0.00%");
        Cell booleanCell = createMockCell(CellType.BOOLEAN, true, null);


        Object stringResult = ImportUtils.cellToObject(stringCell);
        assertEquals("Kowalski", stringResult);

        Object numericResult = ImportUtils.cellToObject(numericCell);
        assertEquals(420, numericResult);

        Object percentageResult = ImportUtils.cellToObject(percentageCell);
        double doubleValue = Double.parseDouble(String.valueOf(percentageResult));
        int intValue = (int) Math.round(doubleValue);
        assertEquals(7, intValue);

        Object booleanResult = ImportUtils.cellToObject(booleanCell);
        assertEquals(true, booleanResult);

        Object nullResult = ImportUtils.cellToObject(null);
        assertEquals("", nullResult);
    }

//    @Test
//    public void testNumericToObject() {
//        Cell cell = createMockCell(CellType.NUMERIC, 420, "dd/MM/yyyy");
//
//        doReturn(true).when(HSSFDateUtil.isCellDateFormatted(cell));
//
//        Date date = Date.from(Instant.parse("2020-12-01T00:00:00.00Z"));
//        when(cell.getDateCellValue()).thenReturn(date);
//
//        CellStyle cellStyle = mock(CellStyle.class);
//        when(cell.getCellStyle()).thenReturn(cellStyle);
//
//        when(cellStyle.getDataFormatString()).thenReturn("dd/MM/yyyy");
//
//        Object result = ImportUtils.numericToObject(cell);
//
//        assertTrue(result instanceof String);
//        assertEquals("01/12/2020", result);
//    }


    @Test
    public void testCapitalizeString() {
        String empty = ImportUtils.capitalizeString("");
        assertEquals("", empty);

        String hyphened = "jan-kowalski-pierwszy-tego-imienia";
        assertEquals("Jan-Kowalski-Pierwszy-Tego-Imienia", ImportUtils.capitalizeString(hyphened));
    }

    @Test
    public void testCleanString() {
        String clean = ImportUtils.cleanString("Hello World");
        assertEquals("Hello World", clean);

        String special = ImportUtils.cleanString("Hello\nWorld\r");
        assertEquals("HelloWorld", special);
    }

    @Test
    public void testGetColumnMap() {
        Row headerRow = mock(Row.class);
        Cell cell1 = mock(Cell.class);
        Cell cell2 = mock(Cell.class);

        when(cell1.getStringCellValue()).thenReturn("Column1");
        when(cell2.getStringCellValue()).thenReturn("Column2");

        List<Cell> cells = new ArrayList<>();
        cells.add(cell1);
        cells.add(cell2);

        when(headerRow.iterator()).thenReturn(cells.iterator());

        Map<String, Integer> columnMap = ImportUtils.getColumnMap(headerRow);

        assertEquals(2, columnMap.size());
        assertTrue(columnMap.containsKey("Column1"));
        assertTrue(columnMap.containsKey("Column2"));
        assertEquals(0, columnMap.get("Column1"));
        assertEquals(0, columnMap.get("Column2"));
    }




}
