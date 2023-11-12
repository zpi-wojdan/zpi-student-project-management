package pwr.zpibackend.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pwr.zpibackend.services.mailing.MailService;
import pwr.zpibackend.utils.MailTemplates;

import java.util.List;

import static pwr.zpibackend.utils.MailTemplates.RESERVATION_LEADER;

@RestController
@RequestMapping("/mail")
@AllArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/simple")
    public void sendSimpleMailMessage(@RequestBody ObjectNode objectNode) {
        String to = objectNode.get("to").asText();
        String subject = objectNode.get("subject").asText();
        String text = objectNode.get("text").asText();
        mailService.sendSimpleMailMessage(to, subject, text);
    }

    @PostMapping("/html")
    public void sendHtmlMailMessage(@RequestBody ObjectNode objectNode) {
        String to = objectNode.get("to").asText();
        String subject = objectNode.get("subject").asText();
        String text = objectNode.get("text").asText();
        String page = "1";
        mailService.sendHtmlMailMessage(to, subject, text, page, RESERVATION_LEADER.getTemplateName());
    }

    @PostMapping("/group")
    public void sendHtmlMailMessageGroup(@RequestBody ObjectNode objectNode) {
        JsonNode to = objectNode.get("to");
        List<String> recipients = to.findValuesAsText("email");
        String subject = objectNode.get("subject").asText();
        String text = objectNode.get("text").asText();
        String page = "1";
        mailService.sendHtmlMailGroupMessage(recipients, subject, text, page, RESERVATION_LEADER.getTemplateName());
    }
}
