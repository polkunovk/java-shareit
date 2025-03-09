package ru.practicum.shareit.server.item.service;

import ru.practicum.shareit.server.item.dto.CommentDto;

public interface CommentService {
    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}

