package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Найти все вещи, принадлежащие конкретному владельцу
    List<Item> findByOwnerId(Long ownerId);

    // Найти все вещи, связанные с определённым запросом
    List<Item> findByRequest_Id(Long requestId); // ✅ Обновили (раньше было `findByRequestId`)

    @Query("SELECT i FROM Item i " +
            "WHERE (UPPER(i.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(CONCAT('%', ?1, '%'))) " +
            "AND i.available = true")
    List<Item> search(String text);
}