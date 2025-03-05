package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                           @Valid @RequestBody ItemDto itemDto,
                           @RequestParam(value = "requestId", required = false) Long requestId) {
        return itemService.addItem(ownerId, itemDto, requestId); // ✅ Теперь передаём 3 аргумента
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable Long id,
                              @RequestBody ItemDto itemDto) {
        return itemService.updateItem(ownerId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                           @PathVariable Long id) {
        return itemService.getItem(id, userId);
    }

    @GetMapping
    public List<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getUserItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return commentService.addComment(userId, itemId, commentDto);
    }
}