package ru.practicum.shareit.server.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.server.item.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
class CommentDtoJsonTest {

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
    void shouldSerializeCommentDto() throws Exception {
        // Создание объекта CommentDto
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Great item!");
        commentDto.setAuthorName("John Doe");
        commentDto.setCreated(LocalDateTime.of(2025, 3, 10, 14, 0));

        // Сериализация в JSON
        String json = objectMapper.writeValueAsString(commentDto);

        // Проверка
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"text\":\"Great item!\"");
        assertThat(json).contains("\"authorName\":\"John Doe\"");
        assertThat(json).contains("\"created\"");
    }

    @Test
    void shouldDeserializeCommentDto() throws Exception {
        // JSON-строка
        String json = "{"
                + "\"id\":1,"
                + "\"text\":\"Great item!\","
                + "\"authorName\":\"John Doe\","
                + "\"created\":\"2025-03-10T14:00:00\""
                + "}";

        // Десериализация JSON
        CommentDto commentDto = objectMapper.readValue(json, CommentDto.class);

        // Проверка
        assertThat(commentDto.getId()).isEqualTo(1L);
        assertThat(commentDto.getText()).isEqualTo("Great item!");
        assertThat(commentDto.getAuthorName()).isEqualTo("John Doe");
        assertThat(commentDto.getCreated()).isEqualTo(LocalDateTime.of(2025, 3, 10, 14, 0));
    }
}
