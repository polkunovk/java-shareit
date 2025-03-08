package ru.practicum.shareit.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.status.BookingStatus;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class DtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testItemDtoSerialization() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Hammer");
        itemDto.setDescription("Heavy hammer");
        itemDto.setAvailable(true);
        itemDto.setRequestId(2L);

        String json = objectMapper.writeValueAsString(itemDto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"Hammer\"");
        assertThat(json).contains("\"description\":\"Heavy hammer\"");
        assertThat(json).contains("\"available\":true");
        assertThat(json).contains("\"requestId\":2");
    }

    @Test
    void testItemDtoDeserialization() throws Exception {
        String json = "{"
                + "\"id\": 1,"
                + "\"name\": \"Hammer\","
                + "\"description\": \"Heavy hammer\","
                + "\"available\": true,"
                + "\"requestId\": 2"
                + "}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Hammer");
        assertThat(itemDto.getDescription()).isEqualTo("Heavy hammer");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(2L);
    }

    @Test
    void testBookingDtoSerialization() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(5L);
        bookingDto.setItemId(10L);
        bookingDto.setStatus(BookingStatus.APPROVED);
        bookingDto.setStart(LocalDateTime.of(2025, 3, 10, 12, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 3, 15, 12, 0));

        String json = objectMapper.writeValueAsString(bookingDto);

        assertThat(json).contains("\"id\":5");
        assertThat(json).contains("\"itemId\":10");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).contains("\"start\":\"2025-03-10T12:00:00\"");
        assertThat(json).contains("\"end\":\"2025-03-15T12:00:00\"");
    }

    @Test
    void testBookingDtoDeserialization() throws Exception {
        String json = "{"
                + "\"id\": 5,"
                + "\"itemId\": 10,"
                + "\"status\": \"APPROVED\","
                + "\"start\": \"2025-03-10T12:00:00\","
                + "\"end\": \"2025-03-15T12:00:00\""
                + "}";

        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        assertThat(bookingDto.getId()).isEqualTo(5L);
        assertThat(bookingDto.getItemId()).isEqualTo(10L);
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2025, 3, 10, 12, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2025, 3, 15, 12, 0));
    }

    @Test
    void testItemRequestDtoSerialization() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(7L);
        requestDto.setDescription("Need a laptop");
        requestDto.setCreated(LocalDateTime.of(2025, 3, 1, 10, 0));

        String json = objectMapper.writeValueAsString(requestDto);

        assertThat(json).contains("\"id\":7");
        assertThat(json).contains("\"description\":\"Need a laptop\"");
        assertThat(json).contains("\"created\":\"2025-03-01T10:00:00\"");
    }

    @Test
    void testItemRequestDtoDeserialization() throws Exception {
        String json = "{"
                + "\"id\": 7,"
                + "\"description\": \"Need a laptop\","
                + "\"created\": \"2025-03-01T10:00:00\""
                + "}";

        ItemRequestDto requestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(requestDto.getId()).isEqualTo(7L);
        assertThat(requestDto.getDescription()).isEqualTo("Need a laptop");
        assertThat(requestDto.getCreated()).isEqualTo(LocalDateTime.of(2025, 3, 1, 10, 0));
    }

    @Test
    void testCommentDtoSerialization() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(3L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("John Doe");
        commentDto.setCreated(LocalDateTime.of(2025, 3, 5, 14, 30));

        String json = objectMapper.writeValueAsString(commentDto);

        assertThat(json).contains("\"id\":3");
        assertThat(json).contains("\"text\":\"Great item!\"");
        assertThat(json).contains("\"authorName\":\"John Doe\"");
        assertThat(json).contains("\"created\":\"2025-03-05T14:30:00\"");
    }

    @Test
    void testCommentDtoDeserialization() throws Exception {
        String json = "{"
                + "\"id\": 3,"
                + "\"text\": \"Great item!\","
                + "\"authorName\": \"John Doe\","
                + "\"created\": \"2025-03-05T14:30:00\""
                + "}";

        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);

        assertThat(commentDto.getId()).isEqualTo(3L);
        assertThat(commentDto.getText()).isEqualTo("Great item!");
        assertThat(commentDto.getAuthorName()).isEqualTo("John Doe");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2025, 3, 5, 14, 30));
    }
}