package ru.practicum.shareit.server.request.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.shareit.server.item.dto.ItemDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}