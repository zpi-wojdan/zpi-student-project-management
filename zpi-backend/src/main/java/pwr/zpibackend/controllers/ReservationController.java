package pwr.zpibackend.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pwr.zpibackend.models.Reservation;
import pwr.zpibackend.services.ReservationService;

import java.util.List;

@RestController
@RequestMapping("/reservation")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservation(@RequestParam Long id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }

    @PostMapping("")
    public ResponseEntity<Reservation> addReservation(@RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.addReservation(reservation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@RequestBody Reservation reservation, @RequestParam Long id) {
        return ResponseEntity.ok(reservationService.updateReservation(reservation, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Reservation> deleteReservation(@RequestParam Long id) {
        return ResponseEntity.ok(reservationService.deleteReservation(id));
    }
}
