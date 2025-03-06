package ru.practicum.shareit.request.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}