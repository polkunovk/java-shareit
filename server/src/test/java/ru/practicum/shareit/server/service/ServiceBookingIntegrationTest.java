package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.booking.status.BookingStatus;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ServiceBookingIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto owner;
    private UserDto booker;
    private ItemDto item;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        // Создание пользователей владельца и бронирующего
        owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        booker = userService.createUser(new UserDto(null, "Booker", "booker@mail.com"));

        // Создание вещи
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        item = itemService.addItem(owner.getId(), itemDto);

        // Создание бронирование
        bookingDto = new BookingDto();
        bookingDto.setItemId(item.getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_shouldSaveAndReturnBooking() {
        // Создание бронирования
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Проверка
        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getId()).isNotNull();
        assertThat(createdBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void createBooking_shouldFailForUnavailableItem() {
        // Делаем вещь недоступной
        item.setAvailable(false);
        itemService.updateItem(owner.getId(), item.getId(), item);

        // Проверка исключения
        assertThatThrownBy(() -> bookingService.createBooking(booker.getId(), bookingDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item is not available for booking");
    }

    @Test
    void approveBooking_shouldUpdateStatusToApproved() {

        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Подтверждение бронирование
        BookingDto approvedBooking = bookingService.approveBooking(owner.getId(), createdBooking.getId(), true);

        // Проверка
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void approveBooking_shouldFailIfNotOwner() {
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Проверка исключения при попытке подтверждения бронирования не владельцем
        assertThatThrownBy(() -> bookingService.approveBooking(booker.getId(), createdBooking.getId(), true))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Only owner can approve bookings");
    }

    @Test
    void getBooking_shouldReturnBookingForOwnerOrBooker() {
        // Создаем бронирование
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Проверка для владельца
        BookingDto foundByOwner = bookingService.getBooking(owner.getId(), createdBooking.getId());
        assertThat(foundByOwner).isNotNull();

        // Проверка для бронирующего
        BookingDto foundByBooker = bookingService.getBooking(booker.getId(), createdBooking.getId());
        assertThat(foundByBooker).isNotNull();
    }

    @Test
    void getBooking_shouldThrowExceptionForUnauthorizedUser() {
        // Создание бронирование
        BookingDto createdBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Создание третьего пользователя
        UserDto otherUser = userService.createUser(new UserDto(null, "Other", "other@mail.com"));

        // Проверка, что сторонний пользователь не может получить бронирование
        assertThatThrownBy(() -> bookingService.getBooking(otherUser.getId(), createdBooking.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Access denied");
    }

    @Test
    void getUserBookings_shouldReturnUserBookings() {
        // Создание нескольких бронирований
        bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.createBooking(booker.getId(), bookingDto);

        // Получение бронирования пользователя
        List<BookingDto> bookings = bookingService.getUserBookings(booker.getId(), "ALL");

        // Проверка
        assertThat(bookings).hasSize(2);
    }

    @Test
    void getOwnerBookings_shouldReturnOwnerBookings() {
        // Создание нескольких бронирований
        bookingService.createBooking(booker.getId(), bookingDto);
        bookingService.createBooking(booker.getId(), bookingDto);

        // Получение бронирования владельца
        List<BookingDto> bookings = bookingService.getOwnerBookings(owner.getId(), "ALL");

        // Проверка
        assertThat(bookings).hasSize(2);
    }

    @Test
    void getUserBookings_shouldReturnPastBookings() {
        // Создание бронирования в прошлом
        bookingDto.setStart(LocalDateTime.now().minusDays(5));
        bookingDto.setEnd(LocalDateTime.now().minusDays(3));
        BookingDto pastBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Подтверждение
        bookingService.approveBooking(owner.getId(), pastBooking.getId(), true);

        // Запрос PAST бронирований
        List<BookingDto> pastBookings = bookingService.getUserBookings(booker.getId(), "PAST");

        // Проверка
        assertThat(pastBookings).hasSize(1);
        assertThat(pastBookings.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void getUserBookings_shouldReturnFutureBookings() {
        // Создание бронирования в будущем
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        BookingDto futureBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Запрос FUTURE бронирований
        List<BookingDto> futureBookings = bookingService.getUserBookings(booker.getId(), "FUTURE");

        // Проверка
        assertThat(futureBookings).hasSize(1);
        assertThat(futureBookings.get(0).getId()).isEqualTo(futureBooking.getId());
    }

    @Test
    void getUserBookings_shouldReturnCurrentBookings() {
        // Создание текущего бронирования
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Подтверждаем бронирование
        bookingService.approveBooking(owner.getId(), currentBooking.getId(), true);

        // Запрос CURRENT бронирований
        List<BookingDto> currentBookings = bookingService.getUserBookings(booker.getId(), "CURRENT");

        // Проверка
        assertThat(currentBookings).hasSize(1);
        assertThat(currentBookings.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void getUserBookings_shouldReturnWaitingBookings() {
        // Создание бронирования с неподтвержденным статусом
        BookingDto waitingBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Запрос WAITING бронирований
        List<BookingDto> waitingBookings = bookingService.getUserBookings(booker.getId(), "WAITING");

        // Проверка
        assertThat(waitingBookings).hasSize(1);
        assertThat(waitingBookings.get(0).getId()).isEqualTo(waitingBooking.getId());
        assertThat(waitingBookings.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getUserBookings_shouldReturnRejectedBookings() {

        BookingDto booking = bookingService.createBooking(booker.getId(), bookingDto);

        // Отклоняем бронирование
        bookingService.approveBooking(owner.getId(), booking.getId(), false);

        // Запрос REJECTED бронирований
        List<BookingDto> rejectedBookings = bookingService.getUserBookings(booker.getId(), "REJECTED");

        // Проверка
        assertThat(rejectedBookings).hasSize(1);
        assertThat(rejectedBookings.get(0).getId()).isEqualTo(booking.getId());
        assertThat(rejectedBookings.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void getOwnerBookings_shouldReturnPastBookings() {
        // Создание бронирования в прошлом
        bookingDto.setStart(LocalDateTime.now().minusDays(5));
        bookingDto.setEnd(LocalDateTime.now().minusDays(3));
        BookingDto pastBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Подтверждение бронирования
        bookingService.approveBooking(owner.getId(), pastBooking.getId(), true);

        // Запрос PAST бронирований владельцем
        List<BookingDto> pastBookings = bookingService.getOwnerBookings(owner.getId(), "PAST");

        // Проверка
        assertThat(pastBookings).hasSize(1);
        assertThat(pastBookings.get(0).getId()).isEqualTo(pastBooking.getId());
    }

    @Test
    void getOwnerBookings_shouldReturnFutureBookings() {
        // Создание бронирования в будущем
        bookingDto.setStart(LocalDateTime.now().plusDays(5));
        bookingDto.setEnd(LocalDateTime.now().plusDays(10));
        BookingDto futureBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Запрос FUTURE бронирований владельцем
        List<BookingDto> futureBookings = bookingService.getOwnerBookings(owner.getId(), "FUTURE");

        // Проверка
        assertThat(futureBookings).hasSize(1);
        assertThat(futureBookings.get(0).getId()).isEqualTo(futureBooking.getId());
    }

    @Test
    void getOwnerBookings_shouldReturnCurrentBookings() {
        // Создание текущего бронирования
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        BookingDto currentBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Подтверждение бронирования
        bookingService.approveBooking(owner.getId(), currentBooking.getId(), true);

        // Запрос CURRENT бронирований владельцем
        List<BookingDto> currentBookings = bookingService.getOwnerBookings(owner.getId(), "CURRENT");

        // Проверка
        assertThat(currentBookings).hasSize(1);
        assertThat(currentBookings.get(0).getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void getOwnerBookings_shouldReturnWaitingBookings() {
        // Создание бронирования со статусом WAITING
        BookingDto waitingBooking = bookingService.createBooking(booker.getId(), bookingDto);

        // Запрос неподтвержденных бронирований владельцем
        List<BookingDto> waitingBookings = bookingService.getOwnerBookings(owner.getId(), "WAITING");

        // Проверка
        assertThat(waitingBookings).hasSize(1);
        assertThat(waitingBookings.get(0).getId()).isEqualTo(waitingBooking.getId());
        assertThat(waitingBookings.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getOwnerBookings_shouldReturnRejectedBookings() {

        BookingDto booking = bookingService.createBooking(booker.getId(), bookingDto);

        bookingService.approveBooking(owner.getId(), booking.getId(), false);

        List<BookingDto> rejectedBookings = bookingService.getOwnerBookings(owner.getId(), "REJECTED");

        // Проверка запроса REJECTED
        assertThat(rejectedBookings).hasSize(1);
        assertThat(rejectedBookings.get(0).getId()).isEqualTo(booking.getId());
        assertThat(rejectedBookings.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
    }
}