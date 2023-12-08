package pwr.zpibackend.controllers.thesis;

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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return new ResponseEntity<>(reservationService.getAllReservations(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<Reservation> getReservation(@PathVariable Long id) {
        return new ResponseEntity<>(reservationService.getReservation(id), HttpStatus.OK);
    }

    @PostMapping("")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<Reservation> addReservation(@RequestBody ReservationDTO reservation) {
        return new ResponseEntity<>(reservationService.addReservation(reservation), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<Reservation> updateReservation(@RequestBody Reservation reservation, @PathVariable Long id) {
        return new ResponseEntity<>(reservationService.updateReservation(reservation, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_STUDENT')")
    public ResponseEntity<Reservation> deleteReservation(@PathVariable Long id) {
        return new ResponseEntity<>(reservationService.deleteReservation(id), HttpStatus.OK);
    }
}
