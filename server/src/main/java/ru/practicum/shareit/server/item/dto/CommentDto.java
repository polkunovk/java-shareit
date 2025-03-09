package ru.practicum.shareit.server.item.dto;

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

    private String text;

    private String authorName;

    private LocalDateTime created;
}

