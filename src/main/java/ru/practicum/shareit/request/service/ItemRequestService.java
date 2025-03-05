package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

public interface ItemRequestService {
    // Создание запроса
    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto);

    // Получение своих запросов
    List<ItemRequestDto> getUserRequests(Long userId);

    // Получение всех запросов других пользователей
    List<ItemRequestDto> getAllRequests(Long userId);

    // Получение конкретного запроса по ID
    ItemRequestDto getRequestById(Long userId, Long requestId);
}
