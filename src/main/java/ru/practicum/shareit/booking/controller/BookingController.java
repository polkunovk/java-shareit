package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public final class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestBody @Valid CreateBookingDto dto) {
        return bookingService.createBooking(bookerId, dto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getBookings(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(defaultValue = "ALL") BookingDto.BookingSearchState state) {
        return bookingService.getBookings(bookerId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "ALL") BookingDto.BookingSearchState state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        return bookingService.approveBooking(bookingId, ownerId, approved);
    }
}