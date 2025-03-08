package ru.practicum.shareit.server.request.service;

import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto);


    List<ItemRequestDto> getUserRequests(Long userId);


    List<ItemRequestDto> getAllRequests(Long userId);


    ItemRequestDto getRequestById(Long userId, Long requestId);
}
