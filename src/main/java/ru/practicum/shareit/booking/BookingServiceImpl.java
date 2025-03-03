package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.interfaces.BookingService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.CreateBookingDto;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto createBooking(long bookerId, CreateBookingDto bookingDto) {
        log.info("Создание бронирования для пользователя id: {} с данными: {}", bookerId, bookingDto);
        final Booking booking = BookingMapper.mapToBooking(bookerId, bookingDto);
        final User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id: {} не найден", bookerId);
                    return new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, bookerId);
                });

        final Long itemId = booking.getItem().getId();
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Вещь с id: {} не найдена", itemId);
                    return new ShareItException(ShareItExceptionCodes.ITEM_NOT_FOUND, itemId);
                });

        if (!item.getAvailable()) {
            log.warn("Вещь с id: {} недоступна для бронирования", itemId);
            throw new ShareItException(ShareItExceptionCodes.ITEM_NOT_AVAILABLE, itemId);
        }

        booking.setBooker(booker);
        booking.setItem(item);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Бронирование успешно создано с id: {}", savedBooking.getId());

        return BookingMapper.mapToBookingDto(savedBooking);
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        log.info("Получение бронирования id: {} для пользователя id: {}", bookingId, userId);
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Бронирование с id: {} не найдено", bookingId);
                    return new ShareItException(ShareItExceptionCodes.BOOKING_NOT_FOUND, bookingId);
                });

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            log.warn("Доступ запрещен для пользователя id: {} для бронирования id: {}", userId, bookingId);
            throw new ShareItException(ShareItExceptionCodes.ACCESS_DENIED, bookingId);
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getBookings(long bookerId, BookingSearchState state) {
        log.info("Получение бронирований для пользователя id: {} с состоянием: {}", bookerId, state);
        if (!userRepository.existsById(bookerId)) {
            log.error("Пользователь с id: {} не найден", bookerId);
            throw new ShareItException(ShareItExceptionCodes.BOOKING_NOT_FOUND, bookerId);
        }

        final LocalDateTime now = LocalDateTime.now();
        final Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
            case CURRENT -> bookingRepository.findByBookerIdAndStartAfterAndEndBeforeOrderByStartDesc(bookerId, now, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED);
        };

        log.debug("Найдено бронирований: {}", bookings.size());
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> getOwnerBookings(long ownerId, BookingSearchState state) {
        log.info("Получение бронирований для владельца id: {} с состоянием: {}", ownerId, state);
        if (!userRepository.existsById(ownerId)) {
            log.error("Пользователь с id: {} не найден", ownerId);
            throw new ShareItException(ShareItExceptionCodes.USER_NOT_FOUND, ownerId);
        }

        final LocalDateTime now = LocalDateTime.now();
        final Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartAfterAndEndBeforeOrderByStartDesc(ownerId, now, now);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED);
        };

        log.debug("Найдено бронирований для владельца: {}", bookings.size());
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public BookingDto approveBooking(long bookingId, long ownerId, boolean approved) {
        log.info("Обновление бронирования id: {} для владельца id: {}. Статус approved: {}",
                bookingId, ownerId, approved);
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Бронирование с id: {} не найдено", bookingId);
                    return new ShareItException(ShareItExceptionCodes.BOOKING_NOT_FOUND, bookingId);
                });

        if (booking.getItem().getOwner().getId() != ownerId) {
            log.warn("Доступ запрещен владельцу id: {} для бронирования id: {}", ownerId, bookingId);
            throw new ShareItException(ShareItExceptionCodes.ACCESS_DENIED, bookingId);
        }

        final BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newStatus);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Бронирование id: {} обновлено. Новый статус: {}", bookingId, newStatus);

        return BookingMapper.mapToBookingDto(updatedBooking);
    }
}
