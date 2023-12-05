package pwr.zpibackend.controllers.thesis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pwr.zpibackend.config.GoogleAuthService;
import pwr.zpibackend.controllers.thesis.ReservationController;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.dto.thesis.ReservationDTO;
import pwr.zpibackend.models.user.Student;
import pwr.zpibackend.models.thesis.Thesis;
import pwr.zpibackend.services.user.EmployeeService;
import pwr.zpibackend.services.thesis.ReservationService;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.services.user.StudentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTests {
    private static final String BASE_URL = "/api/reservation";
    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ReservationService reservationService;
    @MockBean private GoogleAuthService googleAuthService;
    @MockBean private EmployeeService employeeService;
    @MockBean private StudentService studentService;

    private Reservation reservation;
    private ReservationDTO reservationDTO;

    private List<Reservation> reservations;

    @BeforeEach
    public void setUp() {
        reservationDTO = new ReservationDTO();
        reservationDTO.setReservationDate(LocalDateTime.now());
        reservationDTO.setThesisId(1L);
        reservationDTO.setStudent(new Student());
        reservationDTO.setConfirmedByLeader(false);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationDate(LocalDateTime.parse("2023-10-05T12:34:56"));
        reservation.setThesis(mock(Thesis.class));
        reservation.setStudent(new Student());
        reservation.setConfirmedByLeader(false);

        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setReservationDate(LocalDateTime.parse("2023-10-05T12:34:56"));
        reservation2.setThesis(mock(Thesis.class));
        reservation2.setStudent(new Student());
        reservation2.setConfirmedByLeader(false);

        reservations = List.of(reservation, reservation2);
    }

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
        String requestBody = objectMapper.writeValueAsString(reservationDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(reservationService).addReservation(any(ReservationDTO.class));
    }

    @Test
    public void testGetAllReservations() throws Exception {
        Mockito.when(reservationService.getAllReservations()).thenReturn(reservations);

        String resultJson = objectMapper.writeValueAsString(reservations);

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
                .thenReturn(reservation);

        String resultJson = objectMapper.writeValueAsString(reservation);

        mockMvc.perform(get(BASE_URL + "/1")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(resultJson));

        verify(reservationService).getReservation(1L);
    }

    @Test
    public void testUpdateReservationShouldReturnStatusNotFound() throws Exception {
        Reservation newReservation = new Reservation();
        newReservation.setReservationDate(LocalDateTime.parse("2023-10-05T12:34:56"));
        newReservation.setThesis(new Thesis());
        newReservation.setStudent(new Student());
        newReservation.setConfirmedByLeader(false);

        String requestBody = objectMapper.writeValueAsString(newReservation);

        Mockito.doThrow(NotFoundException.class).when(reservationService).updateReservation(any(Reservation.class), eq(1L));

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(reservationService).updateReservation(any(Reservation.class), eq(1L));
    }

    @Test
    public void testUpdateReservationShouldReturnStatusOk() throws Exception {
        Reservation newReservation = new Reservation();
        newReservation.setReservationDate(LocalDateTime.parse("2023-10-05T12:34:56"));
        newReservation.setThesis(new Thesis());
        newReservation.setStudent(new Student());
        newReservation.setConfirmedByLeader(true);

        Mockito.when(reservationService.updateReservation(any(Reservation.class), eq(1L))).thenReturn(newReservation);

        mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(newReservation)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(newReservation)));

        verify(reservationService).updateReservation(any(Reservation.class), eq(1L));
    }

    @Test
    void testDeleteReservation() throws Exception {
        Mockito.when(reservationService.deleteReservation(1L)).thenReturn(new Reservation());

        mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(reservationService).deleteReservation(1L);
    }

}
