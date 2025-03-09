package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.CommentService;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ServiceCommentIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    private UserDto owner;
    private UserDto booker;
    private ItemDto item;
    private BookingDto pastBooking;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        // Создание пользователей
        owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        booker = userService.createUser(new UserDto(null, "Booker", "booker@mail.com"));

        // Создание вещи
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
        item = itemService.addItem(owner.getId(), itemDto);

        // Создание завершенного бронирование
        pastBooking = new BookingDto();
        pastBooking.setItemId(item.getId());
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(2));
        pastBooking = bookingService.createBooking(booker.getId(), pastBooking);

        // Подтверждение бронирование
        bookingService.approveBooking(owner.getId(), pastBooking.getId(), true);

        // Создание комментария
        commentDto = new CommentDto();
        commentDto.setText("Great item!");
    }

    @Test
    void addComment_shouldSaveAndReturnComment() {
        // Добавление комментария
        CommentDto createdComment = commentService.addComment(booker.getId(), item.getId(), commentDto);

        // Проверка
        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getText()).isEqualTo("Great item!");
        assertThat(createdComment.getAuthorName()).isEqualTo(booker.getName());
        assertThat(createdComment.getCreated()).isNotNull();
    }

    @Test
    void addComment_shouldFailIfUserDidNotBookItem() {
        // Создаем пользователя без бронирования
        UserDto otherUser = userService.createUser(new UserDto(null, "OtherUser", "other@mail.com"));

        // Исключение при попытке оставить комментарий без бронирования
        assertThatThrownBy(() -> commentService.addComment(otherUser.getId(), item.getId(), commentDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User has not completed booking for this item");
    }
}