package pwr.zpibackend.services.mailing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MailService {
    public static final String UTF_8_ENCODING = "UTF-8";
    @Value("${spring.mail.verify.host.res_leader}")
    private String host_res_leader;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendSimpleMailMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendHtmlMailMessage(String to, String subject, String text, String page, String template) {
        try {
            Context context = new Context();
            context.setVariables(Map.of(
                    "name", to,
                    "textContents", text,
                    "url", getLinkReservationLeader(page)
            ));
            String html = templateEngine.process(template, context);
            MimeMessage message = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8_ENCODING);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            FileSystemResource image = new FileSystemResource(new File("./src/main/resources/images/logo.png"));
            helper.addInline("image", image);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendHtmlMailGroupMessage(List<String> to, String subject, String text, String page, String template) {
        for (String recipient : to) {
            sendHtmlMailMessage(recipient, subject, text, page, template);
        }
    }

    private MimeMessage getMimeMessage() {
        return javaMailSender.createMimeMessage();
    }

    private String getLinkReservationLeader(String page) {
        return host_res_leader + "/" + page;
    }
}
