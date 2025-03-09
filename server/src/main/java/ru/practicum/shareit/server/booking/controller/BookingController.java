package ru.practicum.shareit.server.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody BookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.createBooking(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam boolean approved) {
        return ResponseEntity.ok(bookingService.approveBooking(ownerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getOwnerBookings(ownerId, state));
    }
}