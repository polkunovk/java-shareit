package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RequestServiceTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User requestor;
    private User anotherUser;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        requestor = userRepository.save(
                User.builder()
                        .name("Requestor")
                        .email("requestor@example.com")
                        .build()
        );

        anotherUser = userRepository.save(
                User.builder()
                        .name("Another User")
                        .email("another@example.com")
                        .build()
        );

        itemRequest = itemRequestRepository.save(
                ItemRequest.builder()
                        .description("Need a laptop")
                        .requestor(requestor)
                        .created(LocalDateTime.now())
                        .build()
        );
    }

    @Test
    void createRequestValidDataShouldSucceed() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a phone");

        ItemRequest newRequest = itemRequestRepository.save(
                ItemRequest.builder()
                        .description(requestDto.getDescription())
                        .requestor(requestor)
                        .created(LocalDateTime.now())
                        .build()
        );

        assertNotNull(newRequest.getId());
        assertEquals("Need a phone", newRequest.getDescription());
    }

    @Test
    void getRequestByIdValidIdShouldReturnRequest() {
        Optional<ItemRequest> foundRequest = itemRequestRepository.findById(itemRequest.getId());

        assertTrue(foundRequest.isPresent());
        assertEquals(itemRequest.getDescription(), foundRequest.get().getDescription());
    }

    @Test
    void getRequestByIdInvalidIdShouldReturnEmpty() {
        Optional<ItemRequest> foundRequest = itemRequestRepository.findById(999L);
        assertFalse(foundRequest.isPresent());
    }

    @Test
    void getUserRequestsShouldReturnRequests() {
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(requestor.getId());

        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(itemRequest.getDescription(), requests.get(0).getDescription());
    }

    @Test
    void getUserRequestsForNonExistingUserShouldReturnEmptyList() {
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(999L);
        assertTrue(requests.isEmpty());
    }

    @Test
    void getAllRequestsShouldReturnRequestsExceptOwn() {
        List<ItemRequest> requests = itemRequestRepository.findAllExceptOwn(anotherUser.getId());

        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(itemRequest.getDescription(), requests.get(0).getDescription());
    }

    @Test
    void getAllRequestsForUserWithoutRequestsShouldReturnEmptyList() {
        List<ItemRequest> requests = itemRequestRepository.findAllExceptOwn(requestor.getId());

        assertTrue(requests.isEmpty());
    }

    @Test
    void deleteRequestByIdShouldRemoveRequest() {
        itemRequestRepository.deleteById(itemRequest.getId());

        Optional<ItemRequest> deletedRequest = itemRequestRepository.findById(itemRequest.getId());
        assertFalse(deletedRequest.isPresent());
    }

    @Test
    void deleteNonExistingRequestShouldNotThrowException() {
        assertDoesNotThrow(() -> itemRequestRepository.deleteById(999L));
    }
}
