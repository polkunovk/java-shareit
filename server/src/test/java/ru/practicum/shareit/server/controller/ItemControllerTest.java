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
import ru.practicum.shareit.server.item.controller.ItemController;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.CommentService;
import ru.practicum.shareit.server.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    @Test
    void addItem_shouldReturnCreatedItem() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);

        ItemDto savedItem = new ItemDto();
        savedItem.setId(1L);
        savedItem.setName("Drill");
        savedItem.setDescription("Powerful drill");
        savedItem.setAvailable(true);

        when(itemService.addItem(anyLong(), any(ItemDto.class))).thenReturn(savedItem);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Drill")))
                .andExpect(jsonPath("$.description", is("Powerful drill")));
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Drill");
        updateDto.setDescription("Even more powerful drill");
        updateDto.setAvailable(false);

        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Drill");
        updatedItem.setDescription("Even more powerful drill");
        updatedItem.setAvailable(false);

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Drill")))
                .andExpect(jsonPath("$.description", is("Even more powerful drill")))
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    void getItem_shouldReturnItem() throws Exception {

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);

        when(itemService.getItem(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Drill")))
                .andExpect(jsonPath("$.description", is("Powerful drill")));
    }

    @Test
    void getUserItems_shouldReturnUserItems() throws Exception {

        ItemDto firstItem = new ItemDto();
        firstItem.setId(1L);
        firstItem.setName("Drill");
        firstItem.setDescription("Powerful drill");
        firstItem.setAvailable(true);

        ItemDto secondItem = new ItemDto();
        secondItem.setId(2L);
        secondItem.setName("Hammer");
        secondItem.setDescription("Heavy hammer");
        secondItem.setAvailable(true);

        List<ItemDto> items = List.of(firstItem, secondItem);

        when(itemService.getUserItems(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Drill")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Hammer")));
    }

    @Test
    void searchItems_shouldReturnMatchingItems() throws Exception {

        ItemDto foundItem = new ItemDto();
        foundItem.setId(1L);
        foundItem.setName("Drill");
        foundItem.setDescription("Powerful drill");
        foundItem.setAvailable(true);

        List<ItemDto> searchResults = List.of(foundItem);

        when(itemService.searchItems(any())).thenReturn(searchResults);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Drill")));
    }

    @Test
    void addComment_shouldReturnCreatedComment() throws Exception {

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great tool!");

        CommentDto savedComment = new CommentDto();
        savedComment.setId(1L);
        savedComment.setText("Great tool!");
        savedComment.setAuthorName("User1");

        when(commentService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(savedComment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Great tool!")))
                .andExpect(jsonPath("$.authorName", is("User1")));
    }
}