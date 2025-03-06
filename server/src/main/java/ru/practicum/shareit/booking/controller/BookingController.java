package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * Создание бронирования
     */
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @Valid @RequestBody BookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.createBooking(userId, bookingDto));
    }

    /**
     * Подтверждение или отклонение бронирования владельцем
     */
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable("bookingId") Long bookingId,
            @RequestParam boolean approved) {
        return ResponseEntity.ok(bookingService.approveBooking(ownerId, bookingId, approved));
    }

    /**
     * Получение информации о бронировании
     */
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(userId, bookingId));
    }

    /**
     * Получение всех бронирований пользователя с фильтрацией по `state`
     */
    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId, state));
    }

    /**
     * Получение всех бронирований владельцем вещи с фильтрацией по `state`
     */
    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getOwnerBookings(ownerId, state));
    }
}