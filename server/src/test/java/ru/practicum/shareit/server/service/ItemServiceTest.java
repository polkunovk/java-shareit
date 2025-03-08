package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.repository.ItemRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User anotherUser;
    private Item item;

    @BeforeEach
    void setUp() {
        user = userRepository.save(
                User.builder()
                        .name("Test User")
                        .email("test@example.com")
                        .build()
        );

        anotherUser = userRepository.save(
                User.builder()
                        .name("Another User")
                        .email("another@example.com")
                        .build()
        );

        item = itemRepository.save(
                Item.builder()
                        .name("Test Item")
                        .description("Description")
                        .available(true)
                        .owner(user)
                        .build()
        );
    }

    @Test
    void addItemValidDataShouldSucceed() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("New Description");
        itemDto.setAvailable(true);

        Item newItem = itemRepository.save(
                Item.builder()
                        .name(itemDto.getName())
                        .description(itemDto.getDescription())
                        .available(itemDto.getAvailable())
                        .owner(user)
                        .build()
        );

        assertNotNull(newItem.getId());
        assertEquals("New Item", newItem.getName());
    }

    @Test
    void getItemByIdValidIdShouldReturnItem() {
        Optional<Item> foundItem = itemRepository.findById(item.getId());

        assertTrue(foundItem.isPresent());
        assertEquals(item.getName(), foundItem.get().getName());
    }

    @Test
    void getItemByIdInvalidIdShouldReturnEmpty() {
        Optional<Item> foundItem = itemRepository.findById(999L);
        assertFalse(foundItem.isPresent());
    }

    @Test
    void updateItemValidDataShouldSucceed() {
        item.setName("Updated Item");
        item.setDescription("Updated Description");
        Item updatedItem = itemRepository.save(item);

        assertEquals("Updated Item", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
    }

    @Test
    void getUserItemsValidUserShouldReturnItems() {
        List<Item> items = itemRepository.findByOwnerId(user.getId());

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals("Test Item", items.get(0).getName());
    }

    @Test
    void getUserItemsNonExistingUserShouldReturnEmptyList() {
        List<Item> items = itemRepository.findByOwnerId(999L);
        assertTrue(items.isEmpty());
    }

    @Test
    void searchItemByNameShouldReturnMatchingItem() {
        List<Item> foundItems = itemRepository.search("Test");

        assertFalse(foundItems.isEmpty());
        assertEquals(1, foundItems.size());
        assertEquals("Test Item", foundItems.get(0).getName());
    }

    @Test
    void searchItemByDescriptionShouldReturnMatchingItem() {
        List<Item> foundItems = itemRepository.search("Description");

        assertFalse(foundItems.isEmpty());
        assertEquals(1, foundItems.size());
        assertEquals("Test Item", foundItems.get(0).getName());
    }

    @Test
    void searchItemWithNonMatchingTextShouldReturnEmptyList() {
        List<Item> foundItems = itemRepository.search("Nonexistent");

        assertTrue(foundItems.isEmpty());
    }

    @Test
    void updateItemAvailabilityShouldSucceed() {
        item.setAvailable(false);
        Item updatedItem = itemRepository.save(item);

        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void deleteItemByIdShouldRemoveItem() {
        itemRepository.deleteById(item.getId());

        Optional<Item> deletedItem = itemRepository.findById(item.getId());
        assertFalse(deletedItem.isPresent());
    }

    @Test
    void deleteNonExistingItemShouldNotThrowException() {
        assertDoesNotThrow(() -> itemRepository.deleteById(999L));
    }
}


