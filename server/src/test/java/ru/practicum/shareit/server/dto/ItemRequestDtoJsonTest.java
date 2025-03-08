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
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
class ItemRequestDtoJsonTest {

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
    void shouldSerializeItemRequestDto() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("I need a drill");
        itemRequestDto.setCreated(LocalDateTime.of(2025, 3, 10, 14, 0));


        String json = objectMapper.writeValueAsString(itemRequestDto);


        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"I need a drill\"");
        assertThat(json).matches(".*(\"created\":\"2025-03-10T14:00:00\"|\"created\":\\[2025,3,10,14,0]).*");
    }

    @Test
    void shouldDeserializeItemRequestDto() throws Exception {

        String json = "{"
                + "\"id\":1,"
                + "\"description\":\"I need a drill\","
                + "\"created\":\"2025-03-10T14:00:00\""
                + "}";


        ItemRequestDto itemRequestDto = objectMapper.readValue(json, ItemRequestDto.class);


        assertThat(itemRequestDto.getId()).isEqualTo(1L);
        assertThat(itemRequestDto.getDescription()).isEqualTo("I need a drill");
        assertThat(itemRequestDto.getCreated()).isEqualTo(LocalDateTime.of(2025, 3, 10, 14, 0));
    }

    @Test
    void shouldFailValidationIfDescriptionIsBlank() {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("");
        itemRequestDto.setCreated(LocalDateTime.now());


        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);


        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Описание запроса не может быть пустым");
    }
}
