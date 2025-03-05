package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull(message = "Доступность вещи должна быть указана")
    private Boolean available;

    @JsonInclude(JsonInclude.Include.NON_NULL) // ✅ Скрываем `requestId`, если `null`
    private Long requestId;

    @JsonInclude(JsonInclude.Include.NON_NULL) // ✅ Скрываем `lastBooking`, если `null`
    private BookingShortDto lastBooking;

    @JsonInclude(JsonInclude.Include.NON_NULL) // ✅ Скрываем `nextBooking`, если `null`
    private BookingShortDto nextBooking;

    @JsonInclude(JsonInclude.Include.NON_EMPTY) // ✅ Скрываем `comments`, если пустой
    private List<CommentDto> comments;
}
