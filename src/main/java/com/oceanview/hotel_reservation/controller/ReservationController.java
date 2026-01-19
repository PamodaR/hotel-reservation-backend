package com.oceanview.hotel_reservation.controller;

import com.oceanview.hotel_reservation.entity.Reservation;
import com.oceanview.hotel_reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<Reservation> addReservation(@RequestBody Reservation reservation) {
        try {
            Reservation saved = service.addReservation(reservation);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Reservation>> getAll() {
        return ResponseEntity.ok(service.getAllReservations());
    }

    @GetMapping("/{reservationNumber}")
    public ResponseEntity<Reservation> getByNumber(@PathVariable String reservationNumber) {
        try {
            Reservation reservation = service.getReservationByNumber(reservationNumber);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{reservationNumber}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable String reservationNumber,
            @RequestBody Reservation reservation) {
        try {
            Reservation updated = service.updateReservation(reservationNumber, reservation);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{reservationNumber}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String reservationNumber) {
        try {
            service.deleteReservation(reservationNumber);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}