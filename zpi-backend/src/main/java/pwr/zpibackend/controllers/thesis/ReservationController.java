package pwr.zpibackend.controllers.thesis;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.models.thesis.Reservation;
import pwr.zpibackend.dto.thesis.ReservationDTO;
import pwr.zpibackend.services.thesis.IReservationService;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {

    private final IReservationService reservationService;

    @GetMapping("")
    @Operation(summary = "Get all reservations", description = "Returns list of all reservations. <br>" +
            "Requires ADMIN, SUPERVISOR or STUDENT role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return new ResponseEntity<>(reservationService.getAllReservations(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by id", description = "Returns reservation with given id. <br>" +
            "Requires ADMIN, SUPERVISOR or STUDENT role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        return new ResponseEntity<>(reservationService.getReservation(id), HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(summary = "Add reservation", description = "Adds reservation to database. <br>" +
            "Requires ADMIN, SUPERVISOR or STUDENT role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationDTO reservation) {
        return new ResponseEntity<>(reservationService.addReservation(reservation), HttpStatus.CREATED);
    }

    @PostMapping("/list")
    @Operation(summary = "Add list of reservations", description = "Adds list of reservations to database. <br>" +
            "Requires ADMIN, SUPERVISOR or STUDENT role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<List<Reservation>> addListReservation(@RequestBody List<ReservationDTO> reservations) {
        return new ResponseEntity<>(reservationService.addListReservation(reservations), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reservation", description = "Updates reservation with given id. <br>" +
            "Requires ADMIN, SUPERVISOR or STUDENT role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<Reservation> updateReservation(@RequestBody Reservation reservation, @PathVariable Long id) {
        return new ResponseEntity<>(reservationService.updateReservation(reservation, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reservation", description = "Deletes reservation with given id. <br>" +
            "Requires ADMIN, SUPERVISOR or STUDENT role.")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<Reservation> deleteReservation(@PathVariable Long id) {
        return new ResponseEntity<>(reservationService.deleteReservation(id), HttpStatus.OK);
    }
}
