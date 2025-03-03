package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.CreateBookingDto;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestBody @Valid CreateBookingDto createBookingDto) {
        return bookingService.createBooking(userId, createBookingDto);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getUserBookings(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = "ALL") BookingSearchState state) {
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBookings(
            @RequestHeader(USER_ID_HEADER) long ownerId,
            @RequestParam(defaultValue = "ALL") BookingSearchState state) {
        return bookingService.getOwnerBookings(ownerId, state);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingApproval(
            @RequestHeader(USER_ID_HEADER) long ownerId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        return bookingService.approveBooking(bookingId, ownerId, approved);
    }
}