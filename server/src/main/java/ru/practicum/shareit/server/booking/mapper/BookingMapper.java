package ru.practicum.shareit.server.booking.mapper;

import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.status.BookingStatus;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.item.mapper.ItemMapper;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem() != null ? booking.getItem().getId() : null)
                .item(booking.getItem() != null ? ItemMapper.toItemDto(booking.getItem()) : null)
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .booker(booking.getBooker() != null ?
                        new UserDto(booking.getBooker().getId(), booking.getBooker().getName(), booking.getBooker().getEmail())
                        : null)
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }
}