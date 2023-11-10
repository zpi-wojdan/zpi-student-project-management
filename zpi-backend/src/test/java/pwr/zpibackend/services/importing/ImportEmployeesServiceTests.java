package pwr.zpibackend.services.importing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pwr.zpibackend.repositories.EmployeeRepository;
import pwr.zpibackend.repositories.RoleRepository;
import pwr.zpibackend.repositories.university.DepartmentRepository;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.mockito.Mockito.mock;

@SpringBootTest(classes = {ImportEmployees.class})
@RunWith(PowerMockRunner.class)
@PrepareForTest({XSSFCell.class})
public class ImportEmployeesServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private DepartmentRepository departmentRepository;

//    @Test
//    public void testReadEmployeeFile() throws IOException {
//        ImportEmployees importEmployees = new ImportEmployees(employeeRepository, roleRepository, departmentRepository);
//        XSSFWorkbook workbook = mock(XSSFWorkbook.class);
//        XSSFSheet sheet = mock(XSSFSheet.class);
//        XSSFRow headerRow = mock(XSSFRow.class);
//        XSSFRow dataRow = mock(XSSFRow.class);
//        XSSFCell cell = PowerMockito.mock(XSSFCell.class);
//
//        List<ObjectNode> validData = new ArrayList<>();
//        List<ObjectNode> invalidIndexData = new ArrayList<>();
//        List<ObjectNode> invalidAcademicTitleData = new ArrayList<>();
//        List<ObjectNode> invalidSurnameData = new ArrayList<>();
//        List<ObjectNode> invalidNameData = new ArrayList<>();
//        List<ObjectNode> invalidUnitData = new ArrayList<>();
//        List<ObjectNode> invalidSubunitData = new ArrayList<>();
//        List<ObjectNode> invalidPositionsData = new ArrayList<>();
//        List<ObjectNode> invalidPhoneNumberData = new ArrayList<>();
//        List<ObjectNode> invalidEmailData = new ArrayList<>();
//
//        Mockito.when(workbook.getSheetAt(0)).thenReturn(sheet);
//        Mockito.when(sheet.getRow(0)).thenReturn(headerRow);
//
//        Mockito.when(cell.getStringCellValue()).thenReturn("Lp.");
//
//        Mockito.when(dataRow.getLastCellNum()).thenReturn((short) 10);
//
//        importEmployees.readEmployeeFile(mock(InputStream.class).toString(), validData, invalidIndexData, invalidAcademicTitleData,
//                invalidSurnameData, invalidNameData, invalidUnitData, invalidSubunitData, invalidPositionsData,
//                invalidPhoneNumberData, invalidEmailData);
//
//        // Assert
//        // Add your assertions based on the behavior you expect from the mocked Excel file.
//    }
//
//    // Define a custom iterator for rows
//    private Iterator<XSSFRow> getCustomRowIterator() {
//        List<XSSFRow> rows = new ArrayList<>();
//        rows.add(Mockito.mock(XSSFRow.class)); // Add header row
//        rows.add(Mockito.mock(XSSFRow.class)); // Add data row
//        return rows.iterator();
//    }

}
