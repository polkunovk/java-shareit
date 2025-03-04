package ru.practicum.shareit.booking;

import org.springframework.core.convert.converter.Converter;
import ru.practicum.shareit.booking.model.BookingDto;

public class SearchStateConverter implements Converter<String, BookingDto.BookingSearchState> {

    @Override
    public BookingDto.BookingSearchState convert(String source) {
        return BookingDto.BookingSearchState.valueOf(source);
    }
}
