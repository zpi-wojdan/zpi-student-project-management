package pwr.zpibackend.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MailTemplates {
    RESERVATION_LEADER("reservationByLeader", "Nowa rezerwacja / New reservation"),
    RESERVATION_STUDENT("reservationByStudent", "Nowa rezerwacja / New reservation"),
    RESERVATION_ADMIN("reservationByAdmin", "Nowa rezerwacja / New reservation"),
    RESERVATION_SUPERVISOR("reservationBySupervisor", "Nowa rezerwacja / New reservation"),
    RESERVATION_SENT_TO_SUPERVISOR("reservationSentToSupervisor", "Grupa zapisana na temat pracy dyplomowej / Group assigned to thesis"),
    RESERVATION_CANCELED("reservationCancelled", "Rezerwacja anulowana / Reservation canceled");

    private final String templateName;
    private final String subject;
}
