package ru.practicum.shareit.server.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.server.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Получение всех комментариев для конкретной вещи
    List<Comment> findByItem_IdOrderByCreatedDesc(Long itemId);

    // Альтернативный метод с возможностью сортировки
    List<Comment> findByItem_Id(Long itemId, Sort sort);

    // Метод для получения последних комментариев с помощью пользовательского JPQL-запроса
    @Query("SELECT c FROM Comment c WHERE c.item.id = :itemId ORDER BY c.created DESC")
    List<Comment> findLatestByItemId(@Param("itemId") Long itemId);
}

