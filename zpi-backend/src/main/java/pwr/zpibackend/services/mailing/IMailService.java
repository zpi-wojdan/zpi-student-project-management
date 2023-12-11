package pwr.zpibackend.services.mailing;

import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.models.user.Employee;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.utils.MailTemplates;

public interface IMailService {
    void sendHtmlMailMessage(String recipient, MailTemplates template, Student student, Employee employee, Thesis thesis);
}
