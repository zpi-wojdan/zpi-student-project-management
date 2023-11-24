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
    public void sendHtmlMailMessage(String recipient, String urlPath, MailTemplates template, String name) {  // te liste arg pewnie też będzie można poprawić później
        try {
            // utworzenie odpowiedniego template html z danymi
            Locale locale = Locale.forLanguageTag("en");        // tu wstawic póżniej program.language() ze studenta
            Context context = new Context(locale);
            context.setVariables(Map.of(
                    "name", name,
                    "thesis", "Temat pracy dyplomowej", // tu będzie do zmiany jak się już podepnie notyfikacje
                    "url", getLinkReservation(urlPath)
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
            helper.setSubject(template.getSubject());
            helper.setText(html, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Async
    public void sendHtmlMailGroupMessage(List<String> recipients, String page, MailTemplates template, List<String> names) {
        for (int i = 0; i < recipients.size(); i++) {
            sendHtmlMailMessage(recipients.get(i), page, template, names.get(i));
        }
    }

    private MimeMessage getMimeMessage() {
        return javaMailSender.createMimeMessage();
    }

    private String getLinkReservation(String page) {
        return host_res_leader + "/" + page;
    }
}
