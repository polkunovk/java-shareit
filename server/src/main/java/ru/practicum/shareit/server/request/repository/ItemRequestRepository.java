package ru.practicum.shareit.server.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // Все запросы, созданные конкретным пользователем
    List<ItemRequest> findByRequestor_IdOrderByCreatedDesc(Long userId);

    @Query("SELECT r FROM ItemRequest r WHERE r.requestor.id <> :userId ORDER BY r.created DESC")
    List<ItemRequest> findAllExceptOwn(@Param("userId") Long userId);
}