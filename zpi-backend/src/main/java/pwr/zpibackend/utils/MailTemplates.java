package pwr.zpibackend.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MailTemplates {
    RESERVATION_LEADER("reservationByLeader", "Nowa rezerwacja"),
    RESERVATION_STUDENT("reservationByStudent", "Nowa rezerwacja"),
    RESERVATION_SUPERVISOR("reservationSentToSupervisor", "Grupa zapisana na temat pracy dyplomowej"),
    THESIS_ADDED("thesisAdded", "Nowy temat pracy dyplomowej");
    private final String templateName;
    private final String subject;
}
