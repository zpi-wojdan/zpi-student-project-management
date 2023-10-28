package pwr.zpibackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.controllers.ReservationController;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.models.dto.ReservationDTO;
import pwr.zpibackend.models.Student;
import pwr.zpibackend.models.Thesis;
import pwr.zpibackend.services.EmployeeService;
import pwr.zpibackend.services.ReservationService;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.services.StudentService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTests {
    private static final String BASE_URL = "/reservation";
    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ReservationService reservationService;
    @MockBean private GoogleAuthService googleAuthService;
    @MockBean private EmployeeService employeeService;
    @MockBean private StudentService studentService;

    @Test
    public void testAddReservationShouldReturnStatusBadRequest() throws Exception {
        ReservationDTO newReservation = new ReservationDTO();
        newReservation.setReservationDate(null);
        newReservation.setThesisId(null);
        newReservation.setStudent(null);

        String requestBody = objectMapper.writeValueAsString(newReservation);

        Mockito.when(reservationService.addReservation(newReservation)).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(reservationService).addReservation(any(ReservationDTO.class));
    }

    @Test
    public void testAddReservationShouldReturnStatusCreated() throws Exception {
        ReservationDTO newReservation = new ReservationDTO();
        newReservation.setReservationDate(LocalDate.parse("2023-10-05"));
        newReservation.setThesisId(1L);
        newReservation.setStudent(new Student());
        newReservation.setConfirmedByLeader(false);

        String requestBody = objectMapper.writeValueAsString(newReservation);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(reservationService).addReservation(any(ReservationDTO.class));
    }

    @Test
    public void testGetAllReservations() throws Exception {
        List<Reservation> reservations = List.of(
                new Reservation(1L, false, false, false, false, LocalDate.parse("2023-10-05"), new Student(), new Thesis()),
                new Reservation(2L, false, false, false, false, LocalDate.parse("2023-10-10"), new Student(), new Thesis())
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
                "\"id\":1," +
                "\"confirmedByLeader\":false," +
                "\"confirmedBySupervisor\":false," +
                "\"confirmedByStudent\":false," +
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
                "\"id\":2," +
                "\"confirmedByLeader\":false," +
                "\"confirmedBySupervisor\":false," +
                "\"confirmedByStudent\":false," +
                "\"readyForApproval\":false}]\n";


        mockMvc.perform(get(BASE_URL)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(reservationService).getAllReservations();
    }

    @Test
    public void testGetReservationByIdShouldReturnStatusNotFound() throws Exception {
        Mockito.when(reservationService.getReservation(1L)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(BASE_URL + "/1")
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        verify(reservationService).getReservation(1L);
    }

    @Test
    public void testGetReservationByIdShouldReturnStatusOk() throws Exception {
        Mockito.when(reservationService.getReservation(1L))
                .thenReturn(new Reservation(
                        1L,
                        false,
                        false,
                        false,
                        false,
                        LocalDate.parse("2023-10-05"),
                        new Student(),
                        new Thesis()));

        String resultJson = "{" +
                "\"reservationDate\":\"2023-10-05\"," +
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
                "\"id\":1," +
                "\"confirmedByLeader\":false," +
                "\"confirmedBySupervisor\":false," +
                "\"confirmedByStudent\":false," +
                "\"readyForApproval\":false}";

        mockMvc.perform(get(BASE_URL + "/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(reservationService).getReservation(1L);
    }

    @Test
    public void testUpdateReservationShouldReturnStatusNotFound() throws Exception {
        Reservation newReservation = new Reservation();
        newReservation.setReservationDate(LocalDate.parse("2023-10-05"));
        newReservation.setThesis(new Thesis());
        newReservation.setStudent(new Student());
        newReservation.setConfirmedByLeader(false);

        String requestBody = objectMapper.writeValueAsString(newReservation);
//
        Mockito.doThrow(NotFoundException.class).when(reservationService).updateReservation(newReservation, 10L);

        mockMvc.perform(put(BASE_URL + "/10")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(reservationService).updateReservation(any(Reservation.class), eq(10L));
    }

    @Test
    public void testUpdateReservationShouldReturnStatusOk() throws Exception {
        Reservation newReservation = new Reservation();
        newReservation.setReservationDate(LocalDate.parse("2023-10-05"));
        newReservation.setThesis(new Thesis());
        newReservation.setStudent(new Student());
        newReservation.setConfirmedByLeader(true);

        Mockito.when(reservationService.updateReservation(any(Reservation.class), eq(1L))).thenReturn(newReservation);

        mockMvc.perform(put("/reservation/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newReservation)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(newReservation)));

        verify(reservationService).updateReservation(any(Reservation.class), eq(1L));
    }

    @Test
    void testDeleteReservation() throws Exception {
        Mockito.when(reservationService.deleteReservation(1L)).thenReturn(new Reservation());

        mockMvc.perform(delete("/reservation/1"))
                .andExpect(status().isOk());

        verify(reservationService).deleteReservation(1L);
    }

}
