package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private static final String SHARER_USER_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader(SHARER_USER_HEADER) Long ownerId
    ) {
        logRequest("добавление", itemDto.getName(), ownerId);
        return itemService.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestBody UpdateItemDto updateDto,
            @RequestHeader(SHARER_USER_HEADER) Long ownerId
    ) {
        logRequest("обновление", itemId.toString(), ownerId);
        return itemService.updateItem(itemId, updateDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ExtendedItemDto getItemById(
            @PathVariable Long itemId,
            @RequestHeader(SHARER_USER_HEADER) Long userId
    ) {
        log.info("Получение вещи ID: {} для пользователя ID: {}", itemId, userId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItemsByOwner(
            @RequestHeader(SHARER_USER_HEADER) Long ownerId
    ) {
        log.info("Запрос всех вещей владельца ID: {}", ownerId);
        return itemService.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(
            @RequestParam String text,
            @RequestHeader(SHARER_USER_HEADER) Long userId
    ) {
        log.info("Поиск по тексту '{}' для пользователя ID: {}", text, userId);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(
            @PathVariable Long itemId,
            @RequestHeader(SHARER_USER_HEADER) Long authorId,
            @Valid @RequestBody CommentDto commentDto
    ) {
        log.info("Добавление комментария к вещи ID: {} от пользователя ID: {}", itemId, authorId);
        return itemService.addCommentToItem(authorId, itemId, commentDto);
    }

    private void logRequest(String action, String target, Long userId) {
        log.info("Запрос на {}: {} пользователем ID: {}", action, target, userId);
    }
}