package ru.practicum.shareit.gateway.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.item.client.ItemClient;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    /**
     * Добавить новую вещь.
     * Если указан requestId, вещь будет связана с запросом.
     */
    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(ownerId, itemDto);
    }

    /**
     * Обновить существующую вещь.
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable("itemId") Long id,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(ownerId, id, itemDto);
    }

    /**
     * Получить информацию о вещи.
     * Если запрашивает владелец, добавляются данные о бронированиях.
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                          @PathVariable("itemId") Long id) {
        return itemClient.getItem(id, userId);
    }

    /**
     * Получить список вещей пользователя.
     */
    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.getUserItems(ownerId);
    }

    /**
     * Поиск вещей по названию и описанию.
     */
    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        return itemClient.searchItems(text);
    }

    /**
     * Оставить комментарий к вещи.
     */
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable("itemId") Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}