package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ServiceItemRequestIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto requestor;
    private UserDto otherUser;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {

        requestor = userService.createUser(new UserDto(null, "Requestor", "requestor@mail.com"));
        otherUser = userService.createUser(new UserDto(null, "OtherUser", "other@mail.com"));


        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Need a drill");
    }

    @Test
    void createRequest_shouldSaveAndReturnRequest() {

        ItemRequestDto createdRequest = itemRequestService.createRequest(requestor.getId(), itemRequestDto);


        assertThat(createdRequest).isNotNull();
        assertThat(createdRequest.getId()).isNotNull();
        assertThat(createdRequest.getDescription()).isEqualTo("Need a drill");
        assertThat(createdRequest.getCreated()).isNotNull();
    }

    @Test
    void getUserRequests_shouldReturnRequestsForUser() {

        itemRequestService.createRequest(requestor.getId(), itemRequestDto);
        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("Need a hammer");
        itemRequestService.createRequest(requestor.getId(), itemRequestDto2);


        List<ItemRequestDto> requests = itemRequestService.getUserRequests(requestor.getId());


        assertThat(requests).hasSize(2);
    }

    @Test
    void getAllRequests_shouldReturnRequestsFromOtherUsers() {

        itemRequestService.createRequest(requestor.getId(), itemRequestDto);


        List<ItemRequestDto> requests = itemRequestService.getAllRequests(otherUser.getId());


        assertThat(requests).hasSize(1);
    }

    @Test
    void getRequestById_shouldReturnRequest() {

        ItemRequestDto createdRequest = itemRequestService.createRequest(requestor.getId(), itemRequestDto);


        ItemRequestDto foundRequest = itemRequestService.getRequestById(requestor.getId(), createdRequest.getId());


        assertThat(foundRequest).isNotNull();
        assertThat(foundRequest.getId()).isEqualTo(createdRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo(createdRequest.getDescription());
    }

    @Test
    void getRequestById_shouldThrowExceptionIfRequestNotFound() {
        assertThatThrownBy(() -> itemRequestService.getRequestById(requestor.getId(), 999L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Request not found");
    }
}

