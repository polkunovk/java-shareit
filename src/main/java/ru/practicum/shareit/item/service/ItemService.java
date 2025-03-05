package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long ownerId, ItemDto itemDto);

    ItemDto getItem(Long id, Long userId);

    ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto);

    List<ItemDto> getUserItems(Long ownerId);

    List<ItemDto> searchItems(String text);

    List<ItemDto> getItemsByRequestId(Long requestId);
}