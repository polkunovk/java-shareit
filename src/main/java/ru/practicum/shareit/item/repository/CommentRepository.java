package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Получение всех комментариев для конкретной вещи с сортировкой по времени создания (новые сверху)
    List<Comment> findByItem_IdOrderByCreatedDesc(Long itemId);

    // Альтернативный метод с возможностью сортировки
    List<Comment> findByItem_Id(Long itemId, Sort sort);

    // Дополнительный метод для получения последних комментариев через кастомный JPQL-запрос
    @Query("SELECT c FROM Comment c WHERE c.item.id = :itemId ORDER BY c.created DESC")
    List<Comment> findLatestByItemId(Long itemId);
}

