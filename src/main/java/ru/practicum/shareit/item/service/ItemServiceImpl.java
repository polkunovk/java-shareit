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
    public ItemDto addItem(Long ownerId, ItemDto itemDto, Long requestId) { // ✅ Добавили requestId
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        ItemRequest itemRequest = null;
        if (requestId != null) { // ✅ Теперь используем параметр requestId вместо itemDto.getRequestId()
            itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NoSuchElementException("Request not found"));
        }

        Item item = ItemMapper.toItem(itemDto, owner, itemRequest); // ✅ Теперь передаём `ItemRequest`

        return ItemMapper.toItemDto(itemRepository.save(item));
    }


    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found"));

        boolean isOwner = item.getOwner().getId().equals(userId);

        Booking lastBooking = isOwner ? bookingRepository
                .findTopByItem_IdAndStartBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .orElse(null) : null;

        Booking nextBooking = isOwner ? bookingRepository
                .findTopByItem_IdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .orElse(null) : null;

        List<CommentDto> comments = commentRepository.findLatestByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemDto(item, lastBooking, nextBooking, comments);
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
}