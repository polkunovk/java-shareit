package ru.practicum.shareit.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.server.booking.controller.BookingController;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.booking.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking_shouldReturnCreatedBooking() throws Exception {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto savedBooking = new BookingDto();
        savedBooking.setId(1L);
        savedBooking.setItemId(1L);
        savedBooking.setStart(bookingDto.getStart());
        savedBooking.setEnd(bookingDto.getEnd());
        savedBooking.setStatus(BookingStatus.WAITING);

        when(bookingService.createBooking(anyLong(), any(BookingDto.class))).thenReturn(savedBooking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void approveBooking_shouldReturnUpdatedBooking() throws Exception {

        BookingDto approvedBooking = new BookingDto();
        approvedBooking.setId(1L);
        approvedBooking.setItemId(1L);
        approvedBooking.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBooking_shouldReturnBooking() throws Exception {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setItemId(1L);
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.itemId", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getUserBookings_shouldReturnUserBookings() throws Exception {

        BookingDto firstBooking = new BookingDto();
        firstBooking.setId(1L);
        firstBooking.setItemId(1L);
        firstBooking.setStatus(BookingStatus.WAITING);

        BookingDto secondBooking = new BookingDto();
        secondBooking.setId(2L);
        secondBooking.setItemId(2L);
        secondBooking.setStatus(BookingStatus.APPROVED);

        List<BookingDto> bookings = List.of(firstBooking, secondBooking);

        when(bookingService.getUserBookings(anyLong(), anyString())).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void getOwnerBookings_shouldReturnOwnerBookings() throws Exception {

        BookingDto ownerBooking = new BookingDto();
        ownerBooking.setId(1L);
        ownerBooking.setItemId(1L);
        ownerBooking.setStatus(BookingStatus.APPROVED);

        List<BookingDto> ownerBookings = List.of(ownerBooking);

        when(bookingService.getOwnerBookings(anyLong(), anyString())).thenReturn(ownerBookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("APPROVED")));
    }
}
