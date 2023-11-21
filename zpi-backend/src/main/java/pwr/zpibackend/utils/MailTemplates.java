package pwr.zpibackend.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MailTemplates {
    RESERVATION_LEADER_PL("reservationByLeader", "Nowa rezerwacja"),
    RESERVATION_STUDENT_PL("reservationByStudent", "Nowa rezerwacja"),
    RESERVATION_SUPERVISOR_PL("reservationSentToSupervisor", "Grupa zapisana na temat pracy dyplomowej"),
    RESERVATION_CANCELED_PL("reservationCanceled", "Rezerwacja anulowana"),
    THESIS_ADDED_PL("thesisAdded", "Nowy temat pracy dyplomowej"),

    RESERVATION_LEADER_EN("reservationByLeader", "New reservation"),
    RESERVATION_STUDENT_EN("reservationByStudent", "New reservation"),
    RESERVATION_SUPERVISOR_EN("reservationSentToSupervisor", "Group assigned to thesis"),
    RESERVATION_CANCELED_EN("reservationCanceled", "Reservation canceled"),
    THESIS_ADDED_EN("thesisAdded", "New thesis added");

    private final String templateName;
    private final String subject;
}
