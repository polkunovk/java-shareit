package ru.practicum.shareit.server.item.controller;

import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                           @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.addItem(ownerId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId,
                                              @PathVariable("itemId") Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(itemService.updateItem(ownerId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(name = "X-Sharer-User-Id", required = false) Long userId,
                                           @PathVariable("itemId") Long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getUserItems(@RequestHeader(name = "X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok(itemService.getUserItems(ownerId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam(name = "text") String text) {
        return ResponseEntity.ok(itemService.searchItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                 @PathVariable("itemId") Long itemId,
                                                 @Valid @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(commentService.addComment(userId, itemId, commentDto));
    }
}