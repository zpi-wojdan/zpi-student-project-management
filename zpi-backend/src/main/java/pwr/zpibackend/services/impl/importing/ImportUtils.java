package pwr.zpibackend.services.impl.importing;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ImportUtils {

    private static final DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

    public static Map<String, Integer> getColumnMap(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        for (Cell cell : headerRow) {
            columnMap.put(cell.getStringCellValue(), cell.getColumnIndex());
        }
        return columnMap;
    }

    static String cleanString(String str) {
        return str.replace("\n", "").replace("\r", "");
    }

    static String capitalizeString(String surname) {
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

    static Object numericToObject(Cell cell){
        if (HSSFDateUtil.isCellDateFormatted(cell)){
            return formatDate.format(cell.getDateCellValue());
        }
        else{
            return (int)cell.getNumericCellValue();
        }
    }

    public static Object cellToObject(Cell cell) {
        if (cell == null) {
            return "";
        }

        CellType type = cell.getCellType();

        if (type == CellType.STRING) {
            String clean = cleanString(cell.getStringCellValue());
            return capitalizeString(clean);
        } else if (type == CellType.NUMERIC) {
            if (cell.getCellStyle() != null && cell.getCellStyle().getDataFormatString() != null
                    && cell.getCellStyle().getDataFormatString().contains("%")) {
                return cell.getNumericCellValue() * 100;
            } else if (HSSFDateUtil.isCellDateFormatted(cell)) {
                return formatDate.format(cell.getDateCellValue());
            } else {
                return (int) cell.getNumericCellValue();
            }
        } else if (type == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        }
        return "";
    }

    public static String createStudentMail(String index) {
        if (index != null && !index.isEmpty()) {
            return index + "@student.pwr.edu.pl";
        } else {
            return index;
        }
    }

    public static boolean isValidIndex(String index) {
        return Pattern.matches("^[1-9]\\d{5}$", index);
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

    public static boolean isValidOrdinalNumber(String lp){
        return Pattern.matches("^\\d{1,5}$", lp);
    }

    public static boolean isValidTeachingCycle(String teachingCycle) {
        return Pattern.matches("^\\d{4}/\\d{2}-[A-Z]{1,3}$", teachingCycle);
    }

    public static boolean isValidStatus(String status) {
        return Pattern.matches("^[A-Z]{1,5}$", status);
    }

    public static boolean isValidAcademicTitle(String academicTitle) {
        return Pattern.matches("^[a-z. ]{0,10}$", academicTitle);
    }

    public static boolean isValidUnit(String unit) {    //  W4N/KP - ????
        return Pattern.matches("^[A-Z0-9]{3,4}", unit);
    }
    public static boolean isValidSubunit(String subunit) {
        return Pattern.matches("^^[A-Z0-9/]{1,10}$", subunit);
    }

    public static boolean isValidPosition(String position) {
        return Pattern.matches("^[0-9a-zA-ZàáâäãåčćèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĆČĖÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ðśŚćĆżŻźŹńŃłŁąĄęĘóÓ ,.\\s()'-]{1,50}$|^$", position);
    }

    public static boolean isValidPhoneNumber(String phone) {
        return Pattern.matches("^(?:([+]?[\\s0-9]+)?(\\d{3}|[(]?[0-9]+[)])?([-]?[\\s]?[0-9])+$|$)", phone);
    }

    public static boolean isValidEmail(String email) {
        return Pattern.matches("^[a-z0-9-]{1,50}(\\.[a-z0-9-]{1,50}){0,4}@(?:student\\.)?(pwr\\.edu\\.pl|pwr\\.wroc\\.pl)$", email);
    }

}
