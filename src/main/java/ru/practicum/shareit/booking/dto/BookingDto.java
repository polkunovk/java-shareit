package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;

    @NotNull(message = "Item ID cannot be null")
    private Long itemId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ItemDto item;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long bookerId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto booker;
    private BookingStatus status;

    public enum BookingSearchState {
        ALL,
        CURRENT,
        PAST,
        FUTURE,
        WAITING,
        REJECTED
    }
}