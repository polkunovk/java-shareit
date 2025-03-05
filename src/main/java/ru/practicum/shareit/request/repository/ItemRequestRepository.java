package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // Найти все запросы, созданные конкретным пользователем (от новых к старым)
    List<ItemRequest> findByRequestor_IdOrderByCreatedDesc(Long userId);

    // Найти все запросы, созданные другими пользователями (от новых к старым)
    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.id <> ?1 ORDER BY r.created DESC")
    List<ItemRequest> findAllExceptOwn(Long userId);
}