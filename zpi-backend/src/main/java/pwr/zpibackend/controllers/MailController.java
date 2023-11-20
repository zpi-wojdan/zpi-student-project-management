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

import static pwr.zpibackend.utils.MailTemplates.RESERVATION_LEADER_PL;

// ten kontroler jest tylko na czas testów jak coś

@RestController
@RequestMapping("/mail")
@AllArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/single")
    public void sendHtmlMailMessage(@RequestBody ObjectNode objectNode) {
        String to = objectNode.get("to").asText();
        String subject = objectNode.get("subject").asText();
        String text = objectNode.get("text").asText();
        String name = objectNode.get("name").asText();
        String page = "1";
        mailService.sendHtmlMailMessage(to, page, RESERVATION_LEADER_PL, name);
    }

    @PostMapping("/group")
    public void sendHtmlMailMessageGroup(@RequestBody ObjectNode objectNode) {
        JsonNode to = objectNode.get("to");
        List<String> recipients = to.findValuesAsText("email");
        List<String> names = to.findValuesAsText("name");
        String subject = objectNode.get("subject").asText();
        String text = objectNode.get("text").asText();
        String page = "1";
        mailService.sendHtmlMailGroupMessage(recipients, page, RESERVATION_LEADER_PL, names);
    }
}
