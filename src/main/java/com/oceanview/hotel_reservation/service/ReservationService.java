package com.oceanview.hotel_reservation.service;

import com.oceanview.hotel_reservation.entity.Reservation;
import com.oceanview.hotel_reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation addReservation(Reservation reservation) {
        // Validate check-in / check-out
        LocalDate checkIn = reservation.getCheckInDate();
        LocalDate checkOut = reservation.getCheckOutDate();

        if (checkIn == null || checkOut == null || checkOut.isBefore(checkIn)) {
            throw new IllegalArgumentException("Invalid check-in or check-out date");
        }

        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        // Generate reservation number
        if (reservation.getReservationNumber() == null) {
            reservation.setReservationNumber("RES" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // Calculate total bill based on room type
        double rate = getRoomRate(reservation.getRoomType());
        reservation.setRoomRate(rate);

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights == 0) {
            nights = 1; // Minimum 1 night
        }

        reservation.setTotalBill(rate * nights);

        return repository.save(reservation);
    }

    private double getRoomRate(String roomType) {
        if (roomType == null) {
            return 5000;
        }

        switch (roomType.toLowerCase()) {
            case "single":
                return 5000;
            case "double":
                return 8000;
            case "suite":
                return 12000;
            case "deluxe":
                return 15000;
            default:
                return 5000;
        }
    }

    public List<Reservation> getAllReservations() {
        return repository.findAll();
    }

    public Reservation getReservationByNumber(String reservationNumber) {
        return repository.findByReservationNumber(reservationNumber)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + reservationNumber));
    }

    public Reservation updateReservation(String reservationNumber, Reservation updatedReservation) {
        Reservation existing = getReservationByNumber(reservationNumber);

        // Update fields
        existing.setGuestName(updatedReservation.getGuestName());
        existing.setEmail(updatedReservation.getEmail());
        existing.setContactNumber(updatedReservation.getContactNumber());
        existing.setAddress(updatedReservation.getAddress());
        existing.setIdNumber(updatedReservation.getIdNumber());
        existing.setNumberOfGuests(updatedReservation.getNumberOfGuests());
        existing.setSpecialRequests(updatedReservation.getSpecialRequests());
        existing.setPaymentMethod(updatedReservation.getPaymentMethod());
        existing.setPaymentStatus(updatedReservation.getPaymentStatus());

        return repository.save(existing);
    }

    public void deleteReservation(String reservationNumber) {
        Reservation reservation = getReservationByNumber(reservationNumber);
        repository.delete(reservation);
    }
}