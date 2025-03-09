package ru.practicum.shareit.server.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.status.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
class BookingDtoJsonTest {

    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldSerializeBookingDto() throws Exception {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(10L);
        bookingDto.setBookerId(20L);
        bookingDto.setStatus(BookingStatus.APPROVED);
        bookingDto.setStart(LocalDateTime.of(2025, 3, 10, 14, 0));
        bookingDto.setEnd(LocalDateTime.of(2025, 3, 11, 14, 0));

        // Сериализация в JSON
        String json = objectMapper.writeValueAsString(bookingDto);

        // Проверка
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"itemId\":10");
        assertThat(json).contains("\"bookerId\":20");
        assertThat(json).contains("\"status\":\"APPROVED\"");
        assertThat(json).matches(".*(\"start\":\"2025-03-10T14:00:00\"|\"start\":\\[2025,3,10,14,0\\]).*");
        assertThat(json).matches(".*(\"end\":\"2025-03-11T14:00:00\"|\"end\":\\[2025,3,11,14,0\\]).*");
    }

    @Test
    void shouldDeserializeBookingDto() throws Exception {
        // JSON-строка
        String json = "{"
                + "\"id\":1,"
                + "\"itemId\":10,"
                + "\"bookerId\":20,"
                + "\"status\":\"APPROVED\","
                + "\"start\":\"2025-03-10T14:00:00\","
                + "\"end\":\"2025-03-11T14:00:00\""
                + "}";

        // Десериализация JSON
        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);

        // Проверка
        assertThat(bookingDto.getId()).isEqualTo(1L);
        assertThat(bookingDto.getItemId()).isEqualTo(10L);
        assertThat(bookingDto.getBookerId()).isEqualTo(20L);
        assertThat(bookingDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(bookingDto.getStart()).isEqualTo(LocalDateTime.of(2025, 3, 10, 14, 0));
        assertThat(bookingDto.getEnd()).isEqualTo(LocalDateTime.of(2025, 3, 11, 14, 0));
    }
}