package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ServiceUserIntegrationTest {

    @Autowired
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("mail@mail.com");
    }

    @Test
    void createUser_shouldSaveAndReturnUser() {
        // Создание пользователя
        UserDto createdUser = userService.createUser(userDto);

        // Проверка
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getName()).isEqualTo(userDto.getName());
        assertThat(createdUser.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void getUser_shouldReturnExistingUser() {
        // Создание пользователя
        UserDto createdUser = userService.createUser(userDto);

        // Получение пользователя
        UserDto foundUser = userService.getUser(createdUser.getId());

        // Проверка
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(createdUser.getId());
        assertThat(foundUser.getName()).isEqualTo(createdUser.getName());
        assertThat(foundUser.getEmail()).isEqualTo(createdUser.getEmail());
    }

    @Test
    void getUser_shouldThrowExceptionWhenNotFound() {
        // Проверка исключения
        assertThatThrownBy(() -> userService.getUser(999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Создание пользователей
        userService.createUser(userDto);
        userService.createUser(new UserDto(null, "User2", "user2@mail.com"));

        // Получение всех пользователей
        List<UserDto> users = userService.getAllUsers();

        // Проверка
        assertThat(users).hasSize(2);
    }

    @Test
    void updateUser_shouldUpdateAndReturnUpdatedUser() {
        // Создание пользователя
        UserDto createdUser = userService.createUser(userDto);

        // Обновление данных
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@mail.com");

        UserDto updatedUser = userService.updateUser(createdUser.getId(), updateDto);

        // Проверка
        assertThat(updatedUser.getId()).isEqualTo(createdUser.getId());
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@mail.com");
    }

    @Test
    void updateUser_shouldThrowExceptionIfUserNotFound() {
        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");

        assertThatThrownBy(() -> userService.updateUser(999L, updateDto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }

    @Test
    void deleteUser_shouldRemoveUser() {
        // Создание пользователя
        UserDto createdUser = userService.createUser(userDto);

        // Удаление пользователя
        userService.deleteUser(createdUser.getId());

        // Проверка отсутствия пользователя
        assertThatThrownBy(() -> userService.getUser(createdUser.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }
}