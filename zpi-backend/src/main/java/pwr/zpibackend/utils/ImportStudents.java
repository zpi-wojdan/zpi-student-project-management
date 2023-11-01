package pwr.zpibackend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ImportStudents {

    public static boolean isValidIndex(String index) {
        return Pattern.matches("^\\d{6}$", index);
    }

    public static boolean isValidSurname(String surname) {
        return Pattern.matches("^[a-zA-ZàáâäãåčćèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĆČĖÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,.\'-]{1,50}$", surname);
    }

    public static boolean isValidName(String name) {
        return Pattern.matches("^[a-zA-ZàáâäãåčćèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĆČĖÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,.\'-]{1,50}$", name);
    }

    public static boolean isValidProgram(String program) {
        return Pattern.matches("^[A-Z0-9]{1,5}-[A-Z]{1,5}-[A-Z0-9]{1,5}-[A-Z0-9]{1,6}$", program);
    }

    public static boolean isValidTeachingCycle(String teachingCycle) {
        return Pattern.matches("^\\d{4}/\\d{2}-[A-Z]{1,3}$", teachingCycle);
    }

    public static boolean isValidStatus(String status) {
        return Pattern.matches("^[A-Z]{1,5}$", status);
    }

    public static void processFile(String file_path) {
        try {
//            String file_path = "C:\\zpi-student-project-management\\zpi-backend\\src\\test\\resources\\ZPI_dane.xlsx";
            List<ObjectNode> validData = new ArrayList<>();
            List<ObjectNode> invalidIndexData = new ArrayList<>();
            List<ObjectNode> invalidSurnameData = new ArrayList<>();
            List<ObjectNode> invalidNameData = new ArrayList<>();
            List<ObjectNode> invalidProgramData = new ArrayList<>();
            List<ObjectNode> invalidTeachingCycleData = new ArrayList<>();
            List<ObjectNode> invalidStatusData = new ArrayList<>();

            readStudentFile(file_path, validData, invalidIndexData, invalidSurnameData, invalidNameData, invalidProgramData, invalidTeachingCycleData, invalidStatusData);

            String fullJson = dataframesToJson(validData, invalidIndexData, invalidSurnameData, invalidNameData, invalidProgramData, invalidTeachingCycleData, invalidStatusData);
            System.out.println("\nFull JSON:");
            System.out.println(fullJson);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readStudentFile(String file_path, List<ObjectNode> validData, List<ObjectNode> invalidIndexData, List<ObjectNode> invalidSurnameData, List<ObjectNode> invalidNameData, List<ObjectNode> invalidProgramData, List<ObjectNode> invalidTeachingCycleData, List<ObjectNode> invalidStatusData) throws IOException {
        FileInputStream excelFile = new FileInputStream(file_path);
        Workbook workbook = new XSSFWorkbook(excelFile);

        Sheet sheet = workbook.getSheetAt(0);
        Map<String, Integer> columnMap = getColumnMap(sheet.getRow(0));

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue;

            boolean isRowEmpty = true;
            for (Cell cell : row) {
                if (cell.getCellType() != CellType.BLANK) {
                    isRowEmpty = false;
                    break;
                }
            }

            if (!isRowEmpty) {
                ObjectNode data = new ObjectMapper().createObjectNode();
                data.put("surname", capitalizeString(row.getCell(columnMap.get("NAZWISKO")).getStringCellValue()));
                data.put("name", capitalizeString(row.getCell(columnMap.get("IMIE")).getStringCellValue()));
                data.put("index", String.valueOf((int) row.getCell(columnMap.get("INDEKS")).getNumericCellValue()));
                data.put("status", row.getCell(columnMap.get("STATUS")).getStringCellValue());
                data.put("role", "student");

                String program = row.getCell(columnMap.get("PROGRAM")).getStringCellValue().toUpperCase();
                String cycle = row.getCell(columnMap.get("CYKL_DYDAKTYCZNY")).getStringCellValue().toUpperCase();

                String[] programParts = program.split(" - ");
                String[] cycleParts = cycle.split(" - ");

                ArrayNode programsCyclesArray = new ObjectMapper().createArrayNode();
                for (int i = 0; i < programParts.length; i++) {
                    ArrayNode cycleData = new ObjectMapper().createArrayNode();
                    cycleData.add(programParts[i]);
                    cycleData.add(cycleParts[i]);
                    programsCyclesArray.add(cycleData);
                }
                data.set("programsCycles", programsCyclesArray);

                data.put("mail", createStudentMail(data.get("index").asText()));

                if (!isValidIndex(data.get("index").asText())) {
                    invalidIndexData.add(data);
                } else if (!isValidSurname(data.get("surname").asText())) {
                    invalidSurnameData.add(data);
                } else if (!isValidName(data.get("name").asText())) {
                    invalidNameData.add(data);
                } else if (!isValidProgram(program)) {
                    invalidProgramData.add(data);
                } else if (!isValidTeachingCycle(cycle)) {
                    invalidTeachingCycleData.add(data);
                } else if (!isValidStatus(data.get("status").asText())) {
                    invalidStatusData.add(data);
                } else {
                    validData.add(data);
                }
            }
        }
        workbook.close();
        excelFile.close();
    }

    public static String capitalizeString(String surname) {
        if (surname != null && !surname.isEmpty()) {
            String[] words = surname.split("-");
            for (int i = 0; i < words.length; i++) {
                words[i] = StringUtils.capitalize(words[i]);
            }
            return String.join("-", words);
        } else {
            return surname;
        }
    }

    public static String createStudentMail(String index) {
        if (index != null && !index.isEmpty()) {
            return index + "@student.pwr.edu.pl";
        } else {
            return index;
        }
    }

    public static Map<String, Integer> getColumnMap(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnMap.put(cell.getStringCellValue(), cell.getColumnIndex());
        }
        return columnMap;
    }

    public static String dataframesToJson(List<ObjectNode> validData, List<ObjectNode> invalidIndexData, List<ObjectNode> invalidSurnameData, List<ObjectNode> invalidNameData, List<ObjectNode> invalidProgramData, List<ObjectNode> invalidTeachingCycleData, List<ObjectNode> invalidStatusData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, List<ObjectNode>> fullJson = new HashMap<>();
            fullJson.put("valid_data", validData);
            fullJson.put("invalid_indices", invalidIndexData);
            fullJson.put("invalid_surnames", invalidSurnameData);
            fullJson.put("invalid_names", invalidNameData);
            fullJson.put("invalid_programs", invalidProgramData);
            fullJson.put("invalid_teaching_cycles", invalidTeachingCycleData);
            fullJson.put("invalid_statuses", invalidStatusData);

            fullJson = mergeFullJson(fullJson);

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fullJson);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Map<String, List<ObjectNode>> mergeFullJson(Map<String, List<ObjectNode>> data) {
        Map<String, List<ObjectNode>> mergedData = new HashMap<>();

        for (Map.Entry<String, List<ObjectNode>> entry : data.entrySet()) {
            String key = entry.getKey();
            List<ObjectNode> value = entry.getValue();

            if (value != null && !value.isEmpty() && value.stream().allMatch(Objects::nonNull)) {
                mergedData.put(key, mergeRowsJson(value));
            } else {
                mergedData.put(key, value);
            }
        }
        return mergedData;
    }

    public static List<ObjectNode> mergeRowsJson(List<ObjectNode> json) {
        Map<String, ObjectNode> mergedData = new HashMap<>();

        for (ObjectNode entry : json) {
            String mail = entry.get("mail").asText();
            JsonNode programsCyclesNode = entry.get("programsCycles");

            if (!mergedData.containsKey(mail)) {
                ObjectNode mergedEntry = entry.deepCopy();
                if (programsCyclesNode.isArray()) {
                    mergedEntry.set("programsCycles", programsCyclesNode);
                } else {
                    ArrayNode cyclesArray = new ObjectMapper().createArrayNode();
                    cyclesArray.add(programsCyclesNode.asText());
                    mergedEntry.set("programsCycles", cyclesArray);
                }
                mergedData.put(mail, mergedEntry);
            } else {
                ObjectNode mergedEntry = mergedData.get(mail);
                ArrayNode programsCyclesArray = (ArrayNode) mergedEntry.get("programsCycles");

                if (programsCyclesNode.isArray()) {
                    programsCyclesArray.addAll((ArrayNode) programsCyclesNode);
                } else {
                    if (!containsCycle(programsCyclesArray, programsCyclesNode.asText())) {
                        programsCyclesArray.add(programsCyclesNode.asText());
                    }
                }
            }
        }
        return new ArrayList<>(mergedData.values());
    }

    private static boolean containsCycle(ArrayNode programsCyclesArray, String cycle) {
        for (JsonNode existingCycle : programsCyclesArray) {
            if (existingCycle.asText().equals(cycle)) {
                return true;
            }
        }
        return false;
    }

}