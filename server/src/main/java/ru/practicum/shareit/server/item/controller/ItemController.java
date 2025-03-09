package ru.practicum.shareit.server.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.item.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    // Добавить новую вещь
    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                           @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.addItem(ownerId, itemDto));
    }

    // Обновить существующую
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                              @PathVariable("itemId") Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.updateItem(ownerId, itemId, itemDto));
    }

    // Если информацию запрашивает владелец, добавляются данные о бронированиях
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
                                           @PathVariable("itemId") Long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId, userId));
    }

    // Получить список вещей пользователя
    @GetMapping
    public ResponseEntity<List<ItemDto>> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok(itemService.getUserItems(ownerId));
    }

    // Поиск вещей по названию и описанию
    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam(name = "text") String text) {
        return ResponseEntity.ok(itemService.searchItems(text));
    }

    // Оставить комментарий к вещи
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @PathVariable("itemId") Long itemId,
                                                 @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(commentService.addComment(userId, itemId, commentDto));
    }
}