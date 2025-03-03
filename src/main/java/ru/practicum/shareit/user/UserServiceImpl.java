package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.model.UserUpdateDto;

import java.util.Collection;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAllUsers() {
        log.debug("Запрос всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto createUser(UserDto dto) {
        User user = UserMapper.mapToUser(dto);
        validateUserFields(user);
        log.debug("Создание пользователя: {}", user.getEmail());
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserUpdateDto dto) {
        validateUserId(userId);
        User existingUser = getUserOrThrow(userId);
        updateUserFields(existingUser, UserMapper.mapUserUpdateDtoToUser(dto));
        log.debug("Обновление пользователя ID: {}", userId);
        return UserMapper.mapToUserDto(userRepository.save(existingUser));
    }

    @Override
    public UserDto getUserById(Long id) {
        validateUserId(id);
        log.debug("Получение пользователя ID: {}", id);
        return UserMapper.mapToUserDto(getUserOrThrow(id));
    }

    @Override
    public void deleteUser(Long id) {
        validateUserId(id);
        log.debug("Удаление пользователя ID: {}", id);
        userRepository.deleteById(id);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(exceptionSupplier(ShareItExceptionCodes.USER_NOT_FOUND, userId));
    }

    private void validateUserId(Long id) {
        if (id == null) {
            log.error("Не указан ID пользователя");
            throw new ShareItException(ShareItExceptionCodes.EMPTY_USER_ID);
        }
    }

    private void validateUserFields(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Пустое имя пользователя");
            throw new ShareItException(ShareItExceptionCodes.EMPTY_USER_NAME);
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Пустой email пользователя");
            throw new ShareItException(ShareItExceptionCodes.EMPTY_USER_EMAIL);
        }
    }

    private void updateUserFields(User existing, User updates) {
        if (updates.getName() != null && !updates.getName().isBlank()) {
            existing.setName(updates.getName());
        }
        if (updates.getEmail() != null && !updates.getEmail().isBlank()) {
            existing.setEmail(updates.getEmail());
        }
    }

    private Supplier<ShareItException> exceptionSupplier(ShareItExceptionCodes code, Object... args) {
        return () -> new ShareItException(code, args);
    }
}