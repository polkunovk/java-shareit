package ru.practicum.shareit.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ServiceItemIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private UserDto owner;
    private UserDto otherUser;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        // Создание пользователей
        owner = userService.createUser(new UserDto(null, "Owner", "owner@mail.com"));
        otherUser = userService.createUser(new UserDto(null, "OtherUser", "other@mail.com"));

        // Создание предмета
        itemDto = new ItemDto();
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
    }

    @Test
    void addItem_shouldSaveAndReturnItem() {

        ItemDto createdItem = itemService.addItem(owner.getId(), itemDto);

        // Проверка создания предмета
        assertThat(createdItem).isNotNull();
        assertThat(createdItem.getId()).isNotNull();
        assertThat(createdItem.getName()).isEqualTo(itemDto.getName());
        assertThat(createdItem.getDescription()).isEqualTo(itemDto.getDescription());
        assertThat(createdItem.getAvailable()).isTrue();
    }

    @Test
    void getItem_shouldReturnExistingItem() {

        ItemDto createdItem = itemService.addItem(owner.getId(), itemDto);

        ItemDto foundItem = itemService.getItem(createdItem.getId(), owner.getId());

        // Проверка получения предмета
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getId()).isEqualTo(createdItem.getId());
        assertThat(foundItem.getName()).isEqualTo(createdItem.getName());
    }

    @Test
    void getItem_shouldThrowExceptionIfItemNotFound() {
        assertThatThrownBy(() -> itemService.getItem(999L, owner.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Item not found");
    }

    @Test
    void updateItem_shouldUpdateItemDetails() {

        ItemDto createdItem = itemService.addItem(owner.getId(), itemDto);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Updated Drill");
        updateDto.setDescription("Even more powerful drill");
        updateDto.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(owner.getId(), createdItem.getId(), updateDto);

        // Проверка обновления предмета
        assertThat(updatedItem.getId()).isEqualTo(createdItem.getId());
        assertThat(updatedItem.getName()).isEqualTo("Updated Drill");
        assertThat(updatedItem.getDescription()).isEqualTo("Even more powerful drill");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    void updateItem_shouldThrowExceptionIfNotOwner() {

        ItemDto createdItem = itemService.addItem(owner.getId(), itemDto);

        // Проверка обновления предмета не владельцем
        ItemDto updateDto = new ItemDto();
        updateDto.setName("Unauthorized Update");

        assertThatThrownBy(() -> itemService.updateItem(otherUser.getId(), createdItem.getId(), updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User is not the owner of this item");
    }

    @Test
    void getUserItems_shouldReturnAllItemsForOwner() {
        // Создание нескольких предметов
        ItemDto firstItem = new ItemDto();
        firstItem.setName("Drill");
        firstItem.setDescription("Powerful drill");
        firstItem.setAvailable(true);
        itemService.addItem(owner.getId(), firstItem);

        ItemDto secondItem = new ItemDto();
        secondItem.setName("Hammer");
        secondItem.setDescription("Heavy hammer");
        secondItem.setAvailable(true);
        itemService.addItem(owner.getId(), secondItem);

        // Получение предметов владельца
        List<ItemDto> items = itemService.getUserItems(owner.getId());

        // Проверка
        assertThat(items).hasSize(2);
    }

    @Test
    void searchItems_shouldReturnMatchingItems() {

        ItemDto firstItem = new ItemDto();
        firstItem.setName("Drill");
        firstItem.setDescription("Powerful drill");
        firstItem.setAvailable(true);
        itemService.addItem(owner.getId(), firstItem);

        ItemDto secondItem = new ItemDto();
        secondItem.setName("Screwdriver");
        secondItem.setDescription("Flathead screwdriver");
        secondItem.setAvailable(true);
        itemService.addItem(owner.getId(), secondItem);

        List<ItemDto> results = itemService.searchItems("Drill");

        // Проверка поиска предметов
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getName()).isEqualTo("Drill");
    }


    @Test
    void searchItems_shouldReturnEmptyListIfNoMatch() {
        // Создание предмета
        itemService.addItem(owner.getId(), itemDto);

        // Поиск предметов по несуществующему запросу
        List<ItemDto> results = itemService.searchItems("Nonexistent");

        // Проверка
        assertThat(results).isEmpty();
    }
}

