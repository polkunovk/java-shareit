package ru.practicum.shareit.server.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.server.booking.status.BookingStatus;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;

    private Long itemId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ItemDto item;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long bookerId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto booker;

    private BookingStatus status;

    private LocalDateTime start;

    private LocalDateTime end;
}