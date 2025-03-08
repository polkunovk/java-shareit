package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(
                User.builder()
                        .name("Test User")
                        .email("test@example.com")
                        .build()
        );
    }

    @Test
    void createUserValidDataShouldSucceed() {
        UserDto userDto = new UserDto();
        userDto.setName("New User");
        userDto.setEmail("new@example.com");

        User newUser = userRepository.save(
                User.builder()
                        .name(userDto.getName())
                        .email(userDto.getEmail())
                        .build()
        );

        assertNotNull(newUser.getId());
        assertEquals("New User", newUser.getName());
    }

    @Test
    void createUserWithDuplicateEmailShouldFail() {
        UserDto userDto = new UserDto();
        userDto.setName("Another User");
        userDto.setEmail(user.getEmail());

        assertThrows(Exception.class, () -> {
            userRepository.save(
                    User.builder()
                            .name(userDto.getName())
                            .email(userDto.getEmail())
                            .build()
            );
        });
    }

    @Test
    void getUserByIdValidIdShouldReturnUser() {
        Optional<User> foundUser = userRepository.findById(user.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void getUserByIdInvalidIdShouldReturnEmpty() {
        Optional<User> foundUser = userRepository.findById(999L);
        assertFalse(foundUser.isPresent());
    }

    @Test
    void getAllUsersShouldReturnUserList() {
        List<User> users = userRepository.findAll();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals(user.getEmail(), users.get(0).getEmail());
    }

    @Test
    void getAllUsersInEmptyDatabaseShouldReturnEmptyList() {
        userRepository.deleteAll();
        List<User> users = userRepository.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void deleteUserByIdShouldRemoveUser() {
        userRepository.deleteById(user.getId());

        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void deleteNonExistingUserShouldNotThrowException() {
        assertDoesNotThrow(() -> userRepository.deleteById(999L));
    }
}

