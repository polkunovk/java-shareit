package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ShareItException;
import ru.practicum.shareit.exception.ShareItExceptionCodes;
import ru.practicum.shareit.item.interfaces.ItemService;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto dto) {
        User owner = getUserOrThrow(userId);
        log.debug("Добавление новой вещи: {} для пользователя ID: {}", dto.getName(), userId);

        Item item = ItemMapper.mapToItem(dto);
        item.setOwner(owner);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(Long itemId, UpdateItemDto dto, Long userId) {
        validateItemId(itemId);
        Item item = getItemOrThrow(itemId);

        validateOwnership(item, userId);
        log.debug("Обновление вещи ID: {} пользователем ID: {}", itemId, userId);

        updateItemFields(item, dto);
        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ExtendedItemDto getItemById(Long itemId) {
        Item item = getItemOrThrow(itemId);
        Collection<Booking> bookings = bookingRepository.findByItemId(itemId);
        return ItemMapper.mapToExtendedItemDto(item, bookings);
    }

    @Override
    public Collection<ItemDto> getAllItemsByOwner(Long ownerId) {
        log.debug("Получение всех вещей владельца ID: {}", ownerId);
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (!isValidSearchQuery(text)) {
            return Collections.emptyList();
        }
        return itemRepository.findByNameOrDescriptionContainingIgnoreCase(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public CommentDto addCommentToItem(long authorId, long itemId, CommentDto commentDto) {
        User author = getUserOrThrow(authorId);
        Item item = getItemOrThrow(itemId);

        validateCommentAuthorization(authorId, itemId);
        log.debug("Добавление комментария к вещи ID: {} от пользователя ID: {}", itemId, authorId);

        Comment comment = createComment(author, item, commentDto);
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private void validateItemId(Long id) {
        if (id == null) {
            log.error("Попытка операции с пустым ID вещи");
            throw new ShareItException(ShareItExceptionCodes.EMPTY_ITEM_ID);
        }
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(exceptionSupplier(ShareItExceptionCodes.USER_NOT_FOUND, userId));
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(exceptionSupplier(ShareItExceptionCodes.ITEM_NOT_FOUND, itemId));
    }

    private void validateOwnership(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            log.error("Пользователь ID: {} не является владельцем вещи ID: {}", userId, item.getId());
            throw new ShareItException(ShareItExceptionCodes.NOT_OWNER_UPDATE);
        }
    }

    private void updateItemFields(Item item, UpdateItemDto dto) {
        if (dto.getName() != null && !dto.getName().isBlank()) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
    }

    private boolean isValidSearchQuery(String text) {
        return text != null && !text.trim().isEmpty();
    }

    private void validateCommentAuthorization(Long authorId, Long itemId) {
        Collection<Booking> bookings = bookingRepository.findByBookerIdAndItemId(authorId, itemId);
        boolean hasCompletedBookings = bookings.stream()
                .anyMatch(b -> b.getEnd().isBefore(LocalDateTime.now()));

        if (!hasCompletedBookings) {
            log.error("Пользователь ID: {} не имеет завершенных бронирований вещи ID: {}", authorId, itemId);
            throw new ShareItException(ShareItExceptionCodes.COMMIT_DENIED, authorId, itemId);
        }
    }

    private Comment createComment(User author, Item item, CommentDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        return comment;
    }

    private Supplier<ShareItException> exceptionSupplier(ShareItExceptionCodes code, Object... args) {
        return () -> new ShareItException(code, args);
    }
}