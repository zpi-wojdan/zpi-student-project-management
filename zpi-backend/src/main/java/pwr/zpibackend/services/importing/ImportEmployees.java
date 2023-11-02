package pwr.zpibackend.services.importing;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Employee;
import pwr.zpibackend.models.Role;
import pwr.zpibackend.models.university.Department;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.repositories.RoleRepository;
import pwr.zpibackend.repositories.university.DepartmentRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class ImportEmployees{

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;

    public void processFile(String file_path) throws IOException{

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

        saveValidToDatabase(validData);
    }

    public void readEmployeeFile(String file_path, List<ObjectNode> validData, List<ObjectNode> invalidIndexData,
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
                String modifiedUnit = "";
                char[] unitCharArray = unit.toCharArray();
                if (unitCharArray.length >= 3){
                    if (Character.isDigit(unitCharArray[1])){
                        if (!Character.isDigit(unitCharArray[2])){
                            char[] modifiedUnitCharArray = new char[unitCharArray.length + 1];
                            modifiedUnitCharArray[0] = unitCharArray[0];
                            modifiedUnitCharArray[1] = '0';
                            System.arraycopy(unitCharArray, 1, modifiedUnitCharArray, 2, unitCharArray.length-1);
                            modifiedUnit = String.valueOf(modifiedUnitCharArray);
                        }
                    }
                }
                if (modifiedUnit.isEmpty()){
                    modifiedUnit = unit;
                }

                String subunit = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Podjednostka")))).toUpperCase();
                String modifiedSubunit = "";
                char[] subunitCharArray = subunit.toCharArray();
                if (subunitCharArray.length >= 3){
                    if (subunitCharArray[0] == 'W'){
                        if (Character.isDigit(subunitCharArray[1])){
                            if (!Character.isDigit(subunitCharArray[2])){
                                char[] modifiedSubunitCharArray = new char[subunitCharArray.length + 1];
                                modifiedSubunitCharArray[0] = subunitCharArray[0];
                                modifiedSubunitCharArray[1] = '0';
                                System.arraycopy(subunitCharArray, 1, modifiedSubunitCharArray, 2, subunitCharArray.length-1);
                                modifiedSubunit = String.valueOf(modifiedSubunitCharArray);
                            }
                        }
                    }
                }
                if (modifiedSubunit.isEmpty()){
                    modifiedSubunit = subunit;
                }


                String position = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Stanowisko"))));
                String phoneNumber = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("Telefon"))));
                String email = String.valueOf(ImportUtils.cellToObject(row.getCell(columns.get("E-mail")))).toLowerCase();
                String role = "supervisor";

                data.put("id", index);
                data.put("title", academicTitle);
                data.put("surname", surname);
                data.put("name", name);
                data.put("faculty", modifiedUnit);
                data.put("department", modifiedSubunit);
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

    public String dataframesToJson(List<ObjectNode> validData, List<ObjectNode> invalidIndexData,
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


    public void saveValidToDatabase(List<ObjectNode> validData){
        List<ObjectNode> invalidData = new ArrayList<>();

        for (ObjectNode node : validData){
            Optional<Employee> existingEmployee = employeeRepository.findByMail(node.get("email").asText());
            Optional<Role> existingRole = roleRepository.findByName(node.get("role").asText());
            Optional<Department> existingDepartment = departmentRepository.findByCode(node.get("department").asText());

            if (existingRole.isEmpty() || existingDepartment.isEmpty()){
                invalidData.add(node);
                continue;
            }

            if (existingEmployee.isEmpty()){
                //  save a new employee to the repository
                Employee employee = new Employee();
                employee.setName(node.get("name").asText());
                employee.setSurname(node.get("surname").asText());
                employee.setMail(node.get("email").asText());
                employee.setTitle(node.get("title").asText());

                //  append the role and department to the employee,
                //  since they are empty, but necessary fields do exist
                //  in the database already
                employee.getRoles().add(existingRole.get());
                employee.setDepartment(existingDepartment.get());

                employeeRepository.save(employee);
            }
            else{
                //  update the existing employee just by appending a new role to him
                List<Role> existingRoles = existingEmployee.get().getRoles();
                boolean roleIsPresent = false;
                for (Role elem : existingRoles){
                    if (elem.getName().equals(node.get("role").asText())){
                        roleIsPresent = true;
                        break;
                    }
                }
                if (!roleIsPresent){
                    existingEmployee.get().getRoles().add(roleRepository.findByName(node.get("role").asText()).get());
                }
            }

        }

        System.out.println("\nValid data:");
        System.out.println(validData.size());

        System.out.println("\nInvalid data:");
        System.out.println(invalidData.size());

        System.out.println("\nEntries added to database:");
        System.out.println(validData.size() - invalidData.size());

    }

}

/*
// Assuming you have the necessary data in your JSON node
JsonNode programsCyclesNode = node.get("programsCycles");

if (programsCyclesNode.isArray()) {
    Set<StudentProgramCycle> studentProgramCycles = new HashSet<>();

    for (JsonNode programCycle : programsCyclesNode) {
        StudentProgramCycle studentProgramCycle = new StudentProgramCycle();

        // Assuming you have the necessary data in your JSON node
        String programName = programCycle.get(0).asText();
        String cycleName = programCycle.get(1).asText();

        // Create new instances for Program and StudyCycle
        Program program = new Program();
        program.setName(programName);
        programRepository.save(program);

        StudyCycle cycle = new StudyCycle();
        cycle.setName(cycleName);
        studyCycleRepository.save(cycle);

        // Set the Program and StudyCycle in StudentProgramCycle
        studentProgramCycle.setProgram(program);
        studentProgramCycle.setCycle(cycle);

        studentProgramCycles.add(studentProgramCycle);
    }

    // Now you can associate studentProgramCycles with your Student entity
    student.setStudentProgramCycles(studentProgramCycles);
}

 */