package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.booking.status.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        // Проверяем, указан ли itemId
        if (bookingDto.getItemId() == null) {
            throw new NoSuchElementException("Item not found");
        }

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("Item not found"));

        // Если вещь недоступна, выбрасываем `IllegalArgumentException`, которая будет обработана как `400 Bad Request`
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item is not available for booking");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can approve bookings");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NoSuchElementException("Access denied");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        BookingState bookingState = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (bookingState) {
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case CURRENT -> bookingRepository.findCurrentBookingsForUser(userId, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        BookingState bookingState = parseState(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (bookingState) {
            case PAST -> bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, now);
            case CURRENT -> bookingRepository.findCurrentBookingsForOwner(ownerId, now);
            case WAITING ->
                    bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
            default -> bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerId);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
    }
}