package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), List.of());
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(userId);

        System.out.println("[getUserRequests] Найдено запросов: " + requests.size());

        return requests.stream()
                .map(request -> {
                    List<ItemDto> items = itemService.getItemsByRequestId(request.getId());
                    System.out.println("[getUserRequests] requestId=" + request.getId() + ", items=" + items);
                    return ItemRequestMapper.toItemRequestDtoWithItems(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<ItemRequest> requests = itemRequestRepository.findAllExceptOwn(userId);

        System.out.println("[getAllRequests] Найдено запросов: " + requests.size());

        return requests.stream()
                .map(request -> {
                    List<ItemDto> items = itemService.getItemsByRequestId(request.getId());
                    System.out.println("[getAllRequests] requestId=" + request.getId() + ", items=" + items);
                    return ItemRequestMapper.toItemRequestDtoWithItems(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found"));

        List<ItemDto> items = itemService.getItemsByRequestId(requestId);
        System.out.println("[getRequestById] items для запроса ID " + requestId + ": " + items.size());

        return ItemRequestMapper.toItemRequestDtoWithItems(itemRequest, items);
    }
}