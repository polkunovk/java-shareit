package ru.practicum.shareit.item.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;

    private String authorName;
    private LocalDateTime created;
}

