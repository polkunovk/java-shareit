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
import ru.practicum.shareit.server.request.controller.ItemRequestController;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");

        ItemRequestDto savedRequest = new ItemRequestDto();
        savedRequest.setId(1L);
        savedRequest.setDescription("Need a drill");
        savedRequest.setCreated(LocalDateTime.now());

        when(itemRequestService.createRequest(anyLong(), any(ItemRequestDto.class))).thenReturn(savedRequest);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Need a drill")))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    void getUserRequests_shouldReturnUserRequests() throws Exception {

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("Need a drill");
        request1.setCreated(LocalDateTime.now());

        ItemRequestDto request2 = new ItemRequestDto();
        request2.setId(2L);
        request2.setDescription("Need a hammer");
        request2.setCreated(LocalDateTime.now());

        List<ItemRequestDto> requests = List.of(request1, request2);

        when(itemRequestService.getUserRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Need a drill")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Need a hammer")));
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() throws Exception {

        ItemRequestDto request1 = new ItemRequestDto();
        request1.setId(1L);
        request1.setDescription("Need a drill");
        request1.setCreated(LocalDateTime.now());

        List<ItemRequestDto> requests = List.of(request1);

        when(itemRequestService.getAllRequests(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Need a drill")));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Need a drill");
        requestDto.setCreated(LocalDateTime.now());

        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Need a drill")))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }
}
