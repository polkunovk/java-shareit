package ru.practicum.shareit.server.request.mapper;

import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.user.model.User;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemDto> items) {
        System.out.println("Mapping request: " + request.getId() + " with " + items.size() + " items");
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto, User requestor) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requestor(requestor)
                .created(requestDto.getCreated())
                .build();
    }

    public static ItemRequestDto toItemRequestDtoWithItems(ItemRequest request, List<ItemDto> items) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items) // Список вещей
                .build();
    }
}