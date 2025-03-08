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
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@JsonTest
class UserDtoJsonTest {

    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldSerializeUserDto() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("john.doe@mail.com");


        String json = objectMapper.writeValueAsString(userDto);


        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"John Doe\"");
        assertThat(json).contains("\"email\":\"john.doe@mail.com\"");
    }

    @Test
    void shouldDeserializeUserDto() throws Exception {

        String json = "{"
                + "\"id\":1,"
                + "\"name\":\"John Doe\","
                + "\"email\":\"john.doe@mail.com\""
                + "}";



        UserDto userDto = objectMapper.readValue(json, UserDto.class);


        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getName()).isEqualTo("John Doe");
        assertThat(userDto.getEmail()).isEqualTo("john.doe@mail.com");
    }

    @Test
    void shouldFailValidationIfNameIsBlank() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("");
        userDto.setEmail("john.doe@mail.com");


        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);


        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Имя пользователя не должно быть пустым");
    }

    @Test
    void shouldFailValidationIfEmailIsBlank() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("");


        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);


        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email не может быть пустым");
    }

    @Test
    void shouldFailValidationIfEmailIsInvalid() {

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("invalid-email");


        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);


        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Некорректный формат email");
    }
}