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
import java.util.Optional;
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
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ItemRequest itemRequest = Optional.ofNullable(itemDto.getRequestId())
                .flatMap(itemRequestRepository::findById)
                .orElse(null);

        Item item = ItemMapper.toItem(itemDto, owner, itemRequest);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

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
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("User is not the owner of this item");
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(existingItem::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(existingItem::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(existingItem::setAvailable);

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Override
    public List<ItemDto> getUserItems(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return itemRepository.findByOwnerId(ownerId).stream()
                .map(item -> {
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
        return text.isBlank() ? List.of() :
                itemRepository.search(text).stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return itemRepository.findByRequest_Id(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}