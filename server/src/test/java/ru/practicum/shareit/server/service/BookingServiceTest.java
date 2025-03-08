package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.repository.BookingRepository;
import ru.practicum.shareit.server.booking.status.BookingStatus;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingServiceTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(
                User.builder()
                        .name("Owner")
                        .email("owner@example.com")
                        .build()
        );

        booker = userRepository.save(
                User.builder()
                        .name("Booker")
                        .email("booker@example.com")
                        .build()
        );

        item = itemRepository.save(
                Item.builder()
                        .name("Test Item")
                        .description("Description")
                        .available(true)
                        .owner(owner)
                        .build()
        );

        booking = bookingRepository.save(
                Booking.builder()
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.WAITING)
                        .build()
        );
    }

    @Test
    void createBookingValidDataShouldSucceed() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(3));
        bookingDto.setEnd(LocalDateTime.now().plusDays(4));

        Booking newBooking = bookingRepository.save(
                Booking.builder()
                        .start(bookingDto.getStart())
                        .end(bookingDto.getEnd())
                        .item(item)
                        .booker(booker)
                        .status(BookingStatus.WAITING)
                        .build()
        );

        assertNotNull(newBooking.getId());
        assertEquals(BookingStatus.WAITING, newBooking.getStatus());
    }

    @Test
    void getBookingByIdValidIdShouldReturnBooking() {
        Optional<Booking> foundBooking = bookingRepository.findById(booking.getId());

        assertTrue(foundBooking.isPresent());
        assertEquals(booking.getId(), foundBooking.get().getId());
    }

    @Test
    void getBookingByIdInvalidIdShouldReturnEmpty() {
        Optional<Booking> foundBooking = bookingRepository.findById(999L);
        assertFalse(foundBooking.isPresent());
    }

    @Test
    void getUserBookingsShouldReturnBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(booker.getId());

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void getUserBookingsForNonExistingUserShouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(999L);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void getOwnerBookingsShouldReturnBookings() {
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(owner.getId());

        assertFalse(bookings.isEmpty());
        assertEquals(1, bookings.size());
    }

    @Test
    void getOwnerBookingsForNonExistingOwnerShouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(999L);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void updateBookingStatusShouldSucceed() {
        booking.setStatus(BookingStatus.APPROVED);
        Booking updatedBooking = bookingRepository.save(booking);

        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void deleteBookingByIdShouldRemoveBooking() {
        bookingRepository.deleteById(booking.getId());

        Optional<Booking> deletedBooking = bookingRepository.findById(booking.getId());
        assertFalse(deletedBooking.isPresent());
    }

    @Test
    void deleteNonExistingBookingShouldNotThrowException() {
        assertDoesNotThrow(() -> bookingRepository.deleteById(999L));
    }
}

