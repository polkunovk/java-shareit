package ru.practicum.shareit.server.item.mapper;

import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName()) // Имя автора комментария
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        return Comment.builder()
                .id(null)
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now()) // Время создания
                .build();
    }
}
