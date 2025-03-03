package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserUpdateDto;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private static final String USER_ID_PATH = "/{userId}";
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        logRequest("получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        logRequest("добавление нового пользователя: " + userDto.getEmail());
        return userService.createUser(userDto);
    }

    @PatchMapping(USER_ID_PATH)
    public UserDto updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateDto updateDto
    ) {
        logRequest("обновление пользователя", userId);
        return userService.updateUser(userId, updateDto);
    }

    @GetMapping(USER_ID_PATH)
    public UserDto getUserById(@PathVariable Long userId) {
        logRequest("получение пользователя", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping(USER_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        logRequest("удаление пользователя", userId);
        userService.deleteUser(userId);
    }

    private void logRequest(String action) {
        log.info("Запрос на {}", action);
    }

    private void logRequest(String action, Long userId) {
        log.info("Запрос на {} с ID: {}", action, userId);
    }
}