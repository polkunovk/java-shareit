package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new IllegalStateException("Email is already in use");
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new IllegalStateException("Email is already in use");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        return UserMapper.toUserDto(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}