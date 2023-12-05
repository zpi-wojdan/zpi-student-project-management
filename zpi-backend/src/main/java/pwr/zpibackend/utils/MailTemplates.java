package pwr.zpibackend.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum MailTemplates {
    RESERVATION_LEADER("reservationByLeader", "Nowa rezerwacja", "New reservation"),
    RESERVATION_STUDENT("reservationByStudent", "Nowa rezerwacja", "New reservation"),
    RESERVATION_ADMIN("reservationByAdmin", "Nowa rezerwacja", "New reservation"),
    RESERVATION_SUPERVISOR("reservationBySupervisor", "Nowa rezerwacja", "New reservation"),
    RESERVATION_SENT_TO_SUPERVISOR("reservationSentToSupervisor", "Grupa zapisana na temat pracy dyplomowej", "Group assigned to thesis"),
    RESERVATION_CANCELED("reservationCancelled", "Rezerwacja anulowana", "Reservation canceled");

    private final String templateName;
    private final Map<String, String> subjectByLanguage;

    MailTemplates(String templateName, String subjectPL, String subjectEN) {
        this.templateName = templateName;
        this.subjectByLanguage = new HashMap<>();
        this.subjectByLanguage.put("pl", subjectPL);
        this.subjectByLanguage.put("en", subjectEN);
    }

    public String getSubject(String language) {
        return subjectByLanguage.getOrDefault(language, "Unknown language");
    }
}
