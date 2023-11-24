package pwr.zpibackend.services.mailing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.utils.MailTemplates;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MailService {
    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String IMAGES_LOGO_PNG = "./src/main/resources/images/logoPl.png";
    @Value("${spring.mail.verify.host.res_leader}")
    private String host_res_leader;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendHtmlMailMessage(String recipient, String urlPath, MailTemplates template, Student student,
                                    Employee employee, Thesis thesis) {
        try {
            // utworzenie odpowiedniego template html z danymi
            String language = thesis.getPrograms().stream().findFirst().get().language();
            String name;
            if (recipient.equals(student.getMail())) {
                name = student.getName() + " " + student.getSurname();
            } else {
                name = employee.getName() + " " + employee.getSurname();
            }

            Locale locale = Locale.forLanguageTag(language);
            Context context = new Context(locale);
            context.setVariables(Map.of(
                    "name", name,
                    "thesis", language.equals("pl") ? thesis.getNamePL() : thesis.getNameEN(),
                    "url", getLinkReservation(urlPath, thesis.getId())
            ));
            String html = templateEngine.process(template.getTemplateName(), context);

            // utworzenie wiadomości mailowej z załącznikiem w formie obrazu (logo pwr)
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);
            FileSystemResource image = new FileSystemResource(new File(IMAGES_LOGO_PNG));
            helper.addInline("image", image);

            // ustawienie parametrów wiadomości
            helper.setFrom(fromEmail);
            helper.setTo(recipient);
            helper.setSubject(template.getSubject(language));
            helper.setText(html, true);

            // wysłanie wiadomości
            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private MimeMessage getMimeMessage() {
        return javaMailSender.createMimeMessage();
    }

    private String getLinkReservation(String page, Long id) {
        return host_res_leader + "/" + page + "/" + id;
    }
}
