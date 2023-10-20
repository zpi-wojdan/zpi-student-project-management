package pwr.zpibackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pwr.zpibackend.controllers.ReservationController;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.services.ReservationService;
import pwr.zpibackend.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTests {
    private static final String BASE_URL = "/reservation";

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ReservationService reservationService;

    @Test
    public void testAddReservationShouldReturnStatusBadRequest() throws Exception {
        Reservation newReservation = new Reservation();
        newReservation.setReservationDate(null);
        newReservation.setThesis(null);
        newReservation.setStudent(null);

        String requestBody = objectMapper.writeValueAsString(newReservation);

        Mockito.when(reservationService.addReservation(newReservation)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddReservationShouldReturnStatusCreated() throws Exception {
        Reservation newReservation = new Reservation();
        newReservation.setReservationDate(LocalDate.parse("2023-10-05"));
        newReservation.setThesis(new Thesis());
        newReservation.setStudent(new Student());
        newReservation.setConfirmedByLeader(false);

        String requestBody = objectMapper.writeValueAsString(newReservation);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAllReservations() throws Exception {
        List<Reservation> reservations = List.of(
                new Reservation(1L, false, false, false, LocalDate.parse("2023-10-05"), new Student(), new Thesis()),
                new Reservation(2L, false, false, false, LocalDate.parse("2023-10-10"), new Student(), new Thesis())
        );

        Mockito.when(reservationService.getAllReservations()).thenReturn(reservations);

        String resultJson = "[" +
                "{\"reservationDate\":\"2023-10-05\"," +
                "\"student\":" +
                    "{\"mail\":null," +
                    "\"name\":null," +
                    "\"surname\":null," +
                    "\"index\":null," +
                    "\"program\":null," +
                    "\"teaching_cycle\":null," +
                    "\"status\":null," +
                    "\"role\":null," +
                    "\"admission_date\":null," +
                    "\"stage\":null" +
                "}," +
                "\"thesis\":{" +
                    "\"namePL\":null," +
                    "\"nameEN\":null," +
                    "\"description\":null," +
                    "\"num_people\":null," +
                    "\"supervisor\":null," +
                    "\"faculty\":null," +
                    "\"field\":null," +
                    "\"edu_cycle\":null," +
                    "\"status\":null," +
                    "\"id\":0" +
                "}," +
                "\"id\":1," +
                "\"confirmedByLeader\":false," +
                "\"confirmedBySupervisor\":false," +
                "\"readyForApproval\":false}," +
                "{\"reservationDate\":\"2023-10-10\"," +
                "\"student\":" +
                    "{\"mail\":null," +
                    "\"name\":null," +
                    "\"surname\":null," +
                    "\"index\":null," +
                    "\"program\":null," +
                    "\"teaching_cycle\":null," +
                    "\"status\":null," +
                    "\"role\":null," +
                    "\"admission_date\":null," +
                    "\"stage\":null}," +
                "\"thesis\":{" +
                    "\"namePL\":null," +
                    "\"nameEN\":null," +
                    "\"description\":null," +
                    "\"num_people\":null," +
                    "\"supervisor\":null," +
                    "\"faculty\":null," +
                    "\"field\":null," +
                    "\"edu_cycle\":null," +
                    "\"status\":null," +
                    "\"id\":0" +
                "}," +
                "\"id\":2," +
                "\"confirmedByLeader\":false," +
                "\"confirmedBySupervisor\":false," +
                "\"readyForApproval\":false}]\n";


        mockMvc.perform(get(BASE_URL)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));
    }

//    te jeszcze nie działają
//    @Test
//    public void testGetReservationByIdShouldReturnStatusNotFound() throws Exception {
//        Mockito.when(reservationService.getReservation(1L)).thenThrow(NotFoundException.class);
//
//        mockMvc.perform(get(BASE_URL + "/1")
//                        .contentType("application/json"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    public void testGetReservationByIdShouldReturnStatusOk() throws Exception {
//        Mockito.when(reservationService.getReservation(1L))
//                .thenReturn(new Reservation(
//                        1L,
//                        false,
//                        false,
//                        false,
//                        LocalDate.parse("2023-10-05"),
//                        new Student(),
//                        new Thesis()));
//
//        String resultJson = "{" +
//                "\"reservationDate\":\"2023-10-05\"," +
//                "\"student\":" +
//                "{\"mail\":null," +
//                "\"name\":null," +
//                "\"surname\":null," +
//                "\"index\":null," +
//                "\"program\":null," +
//                "\"teaching_cycle\":null," +
//                "\"status\":null," +
//                "\"role\":null," +
//                "\"admission_date\":null," +
//                "\"stage\":null" +
//                "}," +
//                "\"thesis\":{" +
//                "\"namePL\":null," +
//                "\"nameEN\":null," +
//                "\"description\":null," +
//                "\"num_people\":null," +
//                "\"supervisor\":null," +
//                "\"faculty\":null," +
//                "\"field\":null," +
//                "\"edu_cycle\":null," +
//                "\"status\":null," +
//                "\"id\":0" +
//                "}," +
//                "\"id\":1," +
//                "\"confirmedByLeader\":false," +
//                "\"confirmedBySupervisor\":false," +
//                "\"readyForApproval\":false}";
//
//        mockMvc.perform(get(BASE_URL + "//1")
//                        .contentType("application/json"))
//                .andExpect(status().isOk())
//                .andExpect(content().json(resultJson));
//    }

}
