package pwr.zpibackend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.exceptions.AlreadyExistsException;
import pwr.zpibackend.exceptions.NotFoundException;
import pwr.zpibackend.exceptions.ThesisOccupancyFullException;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.dto.ReservationDTO;
import pwr.zpibackend.services.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

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
        try {
            return new ResponseEntity<>(reservationService.addReservation(reservation), HttpStatus.CREATED);
        } catch (ThesisOccupancyFullException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
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
