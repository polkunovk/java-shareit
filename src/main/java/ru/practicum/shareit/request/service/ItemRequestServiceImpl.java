package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    private final ItemService itemService; // ✅ Добавляем зависимость для получения вещей по запросу

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

        return itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(userId).stream()
                .map(request -> ItemRequestMapper.toItemRequestDtoWithItems(request, itemService.getItemsByRequestId(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return itemRequestRepository.findAllExceptOwn(userId).stream()
                .map(request -> ItemRequestMapper.toItemRequestDtoWithItems(request, itemService.getItemsByRequestId(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found"));

        List<ItemDto> items = itemService.getItemsByRequestId(requestId);
        return ItemRequestMapper.toItemRequestDtoWithItems(itemRequest, items);
    }
}