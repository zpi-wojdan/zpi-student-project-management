package pwr.zpibackend.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pwr.zpibackend.models.Student;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class PdfService {

    private final StudentService studentService;

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Id", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Surname", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Mail", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Index", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        List<Student> students = studentService.getStudentsWithoutThesis();
        for (Student student : students) {
            table.addCell(String.valueOf(student.getId()));
            table.addCell(student.getName());
            table.addCell(student.getSurname());
            table.addCell(student.getMail());
            table.addCell(String.valueOf(student.getIndex()));
        }
    }

    public void generateStudentsWithoutThesisReport(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("List of students without the thesis", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);

        document.close();
    }

}
