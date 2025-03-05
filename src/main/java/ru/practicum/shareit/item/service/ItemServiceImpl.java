package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Long requestId = itemDto.getRequestId();
        ItemRequest itemRequest = null;

        if (requestId != null) {
            itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NoSuchElementException("Request not found"));
        }

        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);
        item = itemRepository.save(item);

        System.out.println("[addItem] item сохранен в БД: " + item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found"));

        boolean isOwner = item.getOwner().getId().equals(userId);

        System.out.println("[getItem] Получен itemId: " + itemId + ", userId: " + userId);
        System.out.println("[getItem] Владелец вещи: " + item.getOwner().getId() + ", Запрашивающий: " + userId);
        System.out.println("[getItem] Пользователь является владельцем: " + isOwner);

        Booking lastBooking = isOwner ? bookingRepository
                .findTopByItem_IdAndStartBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .orElse(null) : null;

        Booking nextBooking = isOwner ? bookingRepository
                .findTopByItem_IdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .orElse(null) : null;

        System.out.println("[getItem] lastBooking: " + (lastBooking != null ? lastBooking.getId() : "null"));
        System.out.println("[getItem] nextBooking: " + (nextBooking != null ? nextBooking.getId() : "null"));

        List<CommentDto> comments = commentRepository.findLatestByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemDto itemDto = ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);

        System.out.println("[getItem] Возвращаемый itemDto: " + itemDto);

        return itemDto;
    }


    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new NoSuchElementException("User is not the owner of this item");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public List<ItemDto> getUserItems(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        List<Item> items = itemRepository.findByOwnerId(ownerId);

        return items.stream().map(item -> {
            Booking lastBooking = bookingRepository
                    .findTopByItem_IdAndStartBeforeAndStatusOrderByEndDesc(item.getId(), LocalDateTime.now(), BookingStatus.APPROVED)
                    .orElse(null);

            Booking nextBooking = bookingRepository
                    .findTopByItem_IdAndStartAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(), BookingStatus.APPROVED)
                    .orElse(null);

            List<CommentDto> comments = commentRepository.findLatestByItemId(item.getId()).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());

            return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        System.out.println("[getItemsByRequestId] Запрос вещей для requestId=" + requestId);

        List<Item> items = itemRepository.findByRequest_Id(requestId);

        System.out.println("[getItemsByRequestId] Найдено вещей: " + items.size());

        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        System.out.println("🛠️ [getItemsByRequestId] Преобразовано в DTO: " + itemDtos);

        return itemDtos;
    }
}