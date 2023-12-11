package pwr.zpibackend.services.impl.mailing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.university.Program;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.services.mailing.IMailService;
import pwr.zpibackend.utils.MailTemplates;

import javax.mail.internet.MimeMessage;
import java.util.Locale;

@RequiredArgsConstructor
@Service
public class MailService implements IMailService {
    public static final String UTF_8_ENCODING = "UTF-8";
    @Value("${spring.frontend.url}")
    private String host_res_leader;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendHtmlMailMessage(String recipient, MailTemplates template, Student student,
            Employee employee, Thesis thesis) {
        try {
            

            String name;
            if (student != null && recipient.equals(student.getMail())) {
                name = student.getName() + " " + student.getSurname();
            } else {
                name = employee.getName() + " " + employee.getSurname();
            }

            Context context = new Context(Locale.getDefault());
            
            // thesis cannot be null 
            if (thesis == null) {
                throw new IllegalArgumentException("Thesis cannot be null");
            }
            context.setVariable("name", name);
            context.setVariable("thesis_pl", thesis.getNamePL());
            context.setVariable("thesis_en", thesis.getNameEN());
            context.setVariable("url", getLinkReservation(thesis.getId()));

            String html = templateEngine.process(template.getTemplateName(), context);

            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);

            // ustawienie parametrów wiadomości
            helper.setFrom(fromEmail);
            helper.setTo(recipient);
            helper.setSubject(template.getSubject());
            helper.setText(html, true);

            // wysłanie wiadomości
            // javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return javaMailSender.createMimeMessage();
    }

    private String getLinkReservation(Long id) {
        return host_res_leader + "/public-theses/" + id;
    }
}
