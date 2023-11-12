package pwr.zpibackend.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MailTemplates {
    RESERVATION_LEADER("reservationByLeader"),
    RESERVATION_STUDENT("reservationByStudent"),
    RESERVATION_SUPERVISOR("reservationSentToSupervisor"),
    THESIS_ADDED("thesisAdded");
    private final String templateName;
}
