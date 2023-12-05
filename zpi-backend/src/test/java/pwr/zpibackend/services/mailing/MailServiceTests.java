package pwr.zpibackend.services.mailing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.university.StudentProgramCycle;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.utils.MailTemplates;

import javax.mail.internet.MimeMessage;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

public class MailServiceTests {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    public void MailServiceTest() {
        templateEngine = spy(new TemplateEngine());
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendHtmlMailMessageToStudent() {
        String recipient = "test@example.com";
        MailTemplates template = MailTemplates.RESERVATION_STUDENT;
        Student student = mock(Student.class);
        StudentProgramCycle studentProgramCycle = mock(StudentProgramCycle.class);

        when(student.getStudentProgramCycles()).thenReturn(Set.of(studentProgramCycle));
        when(studentProgramCycle.getProgram()).thenReturn(mock(Program.class));
        when(studentProgramCycle.getProgram().language()).thenReturn("pl");
        when(student.getName()).thenReturn("Test student");

        Employee employee = new Employee();
        Thesis thesis = new Thesis();
        thesis.setNamePL("Test thesis");
        thesis.setNameEN("Test thesis");
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailService.sendHtmlMailMessage(recipient, template, student, employee, thesis);
    }

    @Test
    public void testSendHtmlMailMessageToEmployee() {
        String recipient = "test@example.com";
        MailTemplates template = MailTemplates.RESERVATION_STUDENT;
        Student student = mock(Student.class);

        Employee employee = mock(Employee.class);
        Thesis thesis = mock(Thesis.class);
        when(thesis.getPrograms()).thenReturn(List.of(mock(Program.class)));
        when(thesis.getPrograms().stream().findFirst().get().language()).thenReturn("pl");
        when(employee.getName()).thenReturn("Test employee");

        thesis.setNamePL("Test thesis");
        thesis.setNameEN("Test thesis");
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        mailService.sendHtmlMailMessage(recipient, template, null, employee, thesis);
    }
}