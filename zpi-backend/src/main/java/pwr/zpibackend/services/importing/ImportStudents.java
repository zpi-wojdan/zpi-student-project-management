package pwr.zpibackend.services.importing;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Role;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudentProgramCycle;
import pwr.zpibackend.models.university.StudentProgramCycleId;
import pwr.zpibackend.models.university.StudyCycle;
import pwr.zpibackend.repositories.RoleRepository;
import pwr.zpibackend.repositories.StudentRepository;
import pwr.zpibackend.repositories.university.ProgramRepository;
import pwr.zpibackend.repositories.university.StudyCycleRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class ImportStudents {

    private StudentRepository studentRepository;
    private RoleRepository roleRepository;
    private ProgramRepository programRepository;
    private StudyCycleRepository studyCycleRepository;

    public void processFile(String file_path) throws IOException{

//            String file_path = "C:\\zpi-student-project-management\\zpi-backend\\src\\test\\resources\\ZPI_dane.xlsx";
        List<ObjectNode> validData = new ArrayList<>();
        List<ObjectNode> invalidIndexData = new ArrayList<>();
        List<ObjectNode> invalidSurnameData = new ArrayList<>();
        List<ObjectNode> invalidNameData = new ArrayList<>();
        List<ObjectNode> invalidProgramData = new ArrayList<>();
        List<ObjectNode> invalidTeachingCycleData = new ArrayList<>();
        List<ObjectNode> invalidStatusData = new ArrayList<>();

        readStudentFile(file_path, validData, invalidIndexData, invalidSurnameData,
                        invalidNameData, invalidProgramData, invalidTeachingCycleData,
                        invalidStatusData);

        String fullJson = dataframesToJson(validData, invalidIndexData,
                            invalidSurnameData, invalidNameData, invalidProgramData,
                            invalidTeachingCycleData, invalidStatusData);
        System.out.println("\nFull JSON:");
        System.out.println(fullJson);

        saveValidToDatabase(validData);
    }

    public void readStudentFile(String file_path, List<ObjectNode> validData, List<ObjectNode> invalidIndexData,
                                       List<ObjectNode> invalidSurnameData, List<ObjectNode> invalidNameData,
                                       List<ObjectNode> invalidProgramData, List<ObjectNode> invalidTeachingCycleData,
                                       List<ObjectNode> invalidStatusData) throws IOException {
        FileInputStream excelFile = new FileInputStream(file_path);
        Workbook workbook = new XSSFWorkbook(excelFile);

        Sheet sheet = workbook.getSheetAt(0);
        Map<String, Integer> columns = ImportUtils.getColumnMap(sheet.getRow(0));

        if (!columns.containsKey("INDEKS") || !columns.containsKey("NAZWISKO") ||
                !columns.containsKey("IMIE") || !columns.containsKey("PROGRAM") ||
                !columns.containsKey("CYKL_DYDAKTYCZNY") || !columns.containsKey("STATUS") ||
                !columns.containsKey("ETAP")) {
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

                String surname = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("NAZWISKO"))));
                String name = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("IMIE"))));
                String index = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("INDEKS"))));
                String status = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("STATUS"))));
                String role = "student";

                String program = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("PROGRAM")))).toUpperCase();
                String cycle = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("CYKL_DYDAKTYCZNY")))).toUpperCase();
                String[] programParts = program.split(" - ");
                String[] cycleParts = cycle.split(" - ");

                data.put("surname", surname);
                data.put("name", name);
                data.put("index", index);
                data.put("status", status);
                data.put("role", role);

                ArrayNode programsCyclesArray = new ObjectMapper().createArrayNode();
                for (int i = 0; i < programParts.length; i++) {
                    ArrayNode cycleData = new ObjectMapper().createArrayNode();
                    cycleData.add(programParts[i]);
                    cycleData.add(cycleParts[i]);
                    programsCyclesArray.add(cycleData);
                }
                data.set("programsCycles", programsCyclesArray);
                data.put("mail", ImportUtils.createStudentMail(data.get("index").asText()));

                boolean validIndex = ImportUtils.isValidIndex(index);
                boolean validSurname = ImportUtils.isValidSurname(surname);
                boolean validName = ImportUtils.isValidName(name);
                boolean validProgram = ImportUtils.isValidProgram(program);
                boolean validTeachingCycle = ImportUtils.isValidTeachingCycle(cycle);
                boolean validStatus = ImportUtils.isValidStatus(status);

                if (validIndex && validSurname && validName &&
                        validProgram && validTeachingCycle && validStatus){
                    validData.add(data);
                }
                else{
                    if (!validIndex) {
                        invalidIndexData.add(data);
                    }
                    if (!validSurname) {
                        invalidSurnameData.add(data);
                    }
                    if (!validName) {
                        invalidNameData.add(data);
                    }
                    if (!validProgram) {
                        invalidProgramData.add(data);
                    }
                    if (!validTeachingCycle) {
                        invalidTeachingCycleData.add(data);
                    }
                    if (!validStatus) {
                        invalidStatusData.add(data);
                    }
                }
            }
        }
        workbook.close();
        excelFile.close();
    }

    public String dataframesToJson(List<ObjectNode> validData, List<ObjectNode> invalidIndexData,
                                          List<ObjectNode> invalidSurnameData, List<ObjectNode> invalidNameData,
                                          List<ObjectNode> invalidProgramData, List<ObjectNode> invalidTeachingCycleData,
                                          List<ObjectNode> invalidStatusData) throws IOException{
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
    }

    public Map<String, List<ObjectNode>> mergeFullJson(Map<String, List<ObjectNode>> data) {
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

    public List<ObjectNode> mergeRowsJson(List<ObjectNode> json) {
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
                    if (!containsProgramCycleTuple(programsCyclesArray, programsCyclesNode.asText())) {
                        programsCyclesArray.add(programsCyclesNode.asText());
                    }
                }
            }
        }
        return new ArrayList<>(mergedData.values());
    }

    private boolean containsProgramCycleTuple(ArrayNode programsCyclesArray, String cycle) {
        for (JsonNode existingCycle : programsCyclesArray) {
            if (existingCycle.asText().equals(cycle)) {
                return true;
            }
        }
        return false;
    }

    private void saveValidToDatabase(List<ObjectNode> validData){
        List<ObjectNode> invalidData = new ArrayList<>();

        for (ObjectNode node : validData) {
            Optional<Student> existingStudent = studentRepository.findByIndex(node.get("index").asText());
            Optional<Role> existingRole = roleRepository.findByName(node.get("role").asText());

            if (existingRole.isEmpty()){
                invalidData.add(node);
                continue;
            }

            boolean failed = false;
            JsonNode programsCyclesNode = node.get("programsCycles");
            if (existingStudent.isEmpty()) {
                //  save a new student to the repository
                Student student = new Student();
                student.setSurname(node.get("surname").asText());
                student.setName(node.get("name").asText());
                student.setIndex(node.get("index").asText());
                student.setMail(node.get("mail").asText());
                student.setStatus(node.get("status").asText());

                student.setRole(existingRole.get());

                if (programsCyclesNode.isArray()){

                    for (JsonNode elem : programsCyclesNode){
                        StudentProgramCycle studentProgramCycle = new StudentProgramCycle();
                        StudentProgramCycleId studentProgramCycleId = new StudentProgramCycleId();

                        String programName = elem.get(0).asText();
                        String cycleName = elem.get(1).asText();

                        Optional<Program> existingProgram = programRepository.findByName(programName);
                        Optional<StudyCycle> existingStudyCycle = studyCycleRepository.findByName(cycleName);

                        if (existingProgram.isEmpty()){
                            failed = true;
                            break;
                        }
                        else{
                            if (existingStudyCycle.isEmpty()){
                                failed = true;
                                break;
                            }
                            else{
                                studentProgramCycleId.setStudentMail(student.getMail());
                                studentProgramCycleId.setProgramId(existingProgram.get().getId());
                                studentProgramCycleId.setCycleId(existingStudyCycle.get().getId());

                                studentProgramCycle.setCycle(existingStudyCycle.get());
                                studentProgramCycle.setProgram(existingProgram.get());
                                studentProgramCycle.setStudent(student);
                                studentProgramCycle.setId(studentProgramCycleId);

                                student.getStudentProgramCycles().add(studentProgramCycle);
                            }
                        }
                    }
                }
                else {
                    failed = true;
                }
                if (failed){
                    invalidData.add(node);
                }
                else{
                    studentRepository.save(student);
                }
            }
            else{
                Set<StudentProgramCycle> existingStudentProgramCycles = existingStudent.get().getStudentProgramCycles();
                Set<StudentProgramCycle> newStudentProgramCycles = new HashSet<>();

                for (JsonNode elem : programsCyclesNode){
                    String programName = elem.get(0).asText();
                    String cycleName = elem.get(1).asText();

                    boolean isPresent = false;
                    for (StudentProgramCycle existing : existingStudentProgramCycles){
                        if(existing.getProgram().getName().equals(programName) &&
                                existing.getCycle().getName().equals(cycleName)){
                            isPresent = true;
                            break;
                        }
                    }
                    if (!isPresent){
                        Optional<Program> existingProgram = programRepository.findByName(programName);
                        Optional<StudyCycle> existingCycle = studyCycleRepository.findByName(cycleName);

                        if (existingProgram.isEmpty() || existingCycle.isEmpty()){
                            invalidData.add(node);
                            failed = true;
                            break;
                        }

                        StudentProgramCycle newStudProgCyc = new StudentProgramCycle();
                        StudentProgramCycleId id = new StudentProgramCycleId();

                        id.setStudentMail(existingStudent.get().getMail());
                        id.setProgramId(existingProgram.get().getId());
                        id.setCycleId(existingCycle.get().getId());

                        newStudProgCyc.setProgram(existingProgram.get());
                        newStudProgCyc.setCycle(existingCycle.get());
                        newStudProgCyc.setStudent(existingStudent.get());
                        newStudProgCyc.setId(id);

                        newStudentProgramCycles.add(newStudProgCyc);
                    }
                }
                if (!failed){
                    existingStudent.get().getStudentProgramCycles().addAll(newStudentProgramCycles);
                    studentRepository.save(existingStudent.get()); // Save the updated student
                }
            }
        }
    }

}