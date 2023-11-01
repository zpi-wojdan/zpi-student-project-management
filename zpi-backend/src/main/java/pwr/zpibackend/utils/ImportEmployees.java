package pwr.zpibackend.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ImportEmployees{

    public static void processFile(String file_path) throws IOException{

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

        readEmployeeFile(file_path, validData, invalidIndexData, invalidAcademicTitleData,
                        invalidSurnameData, invalidNameData, invalidUnitData, invalidSubunitData,
                        invalidPositionsData, invalidPhoneNumberData, invalidEmailData);

        String fullJson = dataframesToJson(validData, invalidIndexData, invalidAcademicTitleData,
                            invalidSurnameData, invalidNameData, invalidUnitData, invalidSubunitData,
                            invalidPositionsData, invalidPhoneNumberData, invalidEmailData);
        System.out.println("\nFull JSON:");
        System.out.println(fullJson);
    }

    public static void readEmployeeFile(String file_path, List<ObjectNode> validData, List<ObjectNode> invalidIndexData,
                                        List<ObjectNode> invalidAcademicTitleData, List<ObjectNode> invalidSurnameData,
                                        List<ObjectNode> invalidNameData, List<ObjectNode> invalidUnitData,
                                        List<ObjectNode> invalidSubunitData, List<ObjectNode> invalidPositionsData,
                                        List<ObjectNode> invalidPhoneNumberData, List<ObjectNode> invalidEmailData) throws IOException {
        FileInputStream excelFile = new FileInputStream(file_path);
        Workbook workbook = new XSSFWorkbook(excelFile);

        Sheet sheet = workbook.getSheetAt(0);
        Map<String, Integer> columns = ImportUtils.getColumnMap(sheet.getRow(0));

        if (!columns.containsKey("Lp.") || !columns.containsKey("Tytuł/stopień") ||
                !columns.containsKey("Nazwisko") || !columns.containsKey("Imię") ||
                !columns.containsKey("Jednostka") || !columns.containsKey("Podjednostka") ||
                !columns.containsKey("Stanowisko") || !columns.containsKey("Telefon") ||
                !columns.containsKey("E-mail")) {
            throw new IOException("Missing required columns");
        }

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            boolean isRowEmpty = true;
            for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                if (cell != null && cell.getCellType() != CellType.BLANK) {
                    isRowEmpty = false;
                    break;
                }
            }

            if (!isRowEmpty) {
                ObjectNode data = new ObjectMapper().createObjectNode();

                String index = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Lp."))));
                String academicTitle = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Tytuł/stopień")))).toLowerCase();
                String surname = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Nazwisko"))));
                String name = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Imię"))));
                String unit = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Jednostka")))).toUpperCase();
                String subunit = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Podjednostka")))).toUpperCase();
                String position = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Stanowisko"))));
                String phoneNumber = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Telefon"))));
                String email = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("E-mail")))).toLowerCase();
                String role = "employee";

                data.put("id", index);
                data.put("title", academicTitle);
                data.put("surname", surname);
                data.put("name", name);
                data.put("faculty", unit);
                data.put("department", subunit);
                data.put("position", position);
                data.put("phoneNumber", phoneNumber);
                data.put("email", email);
                data.put("role", role);

                boolean validIndex = ImportUtils.isValidOrdinalNumber(index);
                boolean validTitle = ImportUtils.isValidAcademicTitle(academicTitle);
                boolean validSurname = ImportUtils.isValidSurname(surname);
                boolean validName = ImportUtils.isValidName(name);
                boolean validUnit = ImportUtils.isValidUnit(unit);
                boolean validSubunit = ImportUtils.isValidSubunit(subunit);
                boolean validPosition = ImportUtils.isValidPosition(position);
                boolean validPhoneNumber = ImportUtils.isValidPhoneNumber(phoneNumber);
                boolean validEmail = ImportUtils.isValidEmail(email);

                if (validIndex && validTitle && validSurname &&
                        validName && validUnit && validSubunit &&
                        validPosition && validPhoneNumber && validEmail) {
                    validData.add(data);
                }
                else{
                    if (!validIndex) {
                        invalidIndexData.add(data);
                    }
                    if (!validTitle) {
                        invalidAcademicTitleData.add(data);
                    }
                    if (!validSurname) {
                        invalidSurnameData.add(data);
                    }
                    if (!validName) {
                        invalidNameData.add(data);
                    }
                    if (!validUnit) {
                        invalidUnitData.add(data);
                    }
                    if (!validSubunit) {
                        invalidSubunitData.add(data);
                    }
                    if (!validPosition) {
                        invalidPositionsData.add(data);
                    }
                    if (!validPhoneNumber) {
                        invalidPhoneNumberData.add(data);
                    }
                    if (!validEmail) {
                        invalidEmailData.add(data);
                    }
                }
            }
        }
        workbook.close();
        excelFile.close();
    }

    public static String dataframesToJson(List<ObjectNode> validData, List<ObjectNode> invalidIndexData,
                                          List<ObjectNode> invalidAcademicTitleData, List<ObjectNode> invalidSurnameData,
                                          List<ObjectNode> invalidNameData, List<ObjectNode> invalidUnitData,
                                          List<ObjectNode> invalidSubunitData, List<ObjectNode> invalidPositionsData,
                                          List<ObjectNode> invalidPhoneNumberData, List<ObjectNode> invalidEmailData) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, List<ObjectNode>> fullJson = new HashMap<>();
        fullJson.put("valid_data", validData);
        fullJson.put("invalid_indices", invalidIndexData);
        fullJson.put("invalid_academic_titles", invalidAcademicTitleData);
        fullJson.put("invalid_surnames", invalidSurnameData);
        fullJson.put("invalid_names", invalidNameData);
        fullJson.put("invalid_units", invalidUnitData);
        fullJson.put("invalid_subunits", invalidSubunitData);
        fullJson.put("invalid_positions", invalidPositionsData);
        fullJson.put("invalid_phone_numbers", invalidPhoneNumberData);
        fullJson.put("invalid_emails", invalidEmailData);

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fullJson);
    }

}