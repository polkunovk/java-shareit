package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItem_IdOrderByCreatedDesc(Long itemId);

    List<Comment> findByItem_Id(Long itemId, Sort sort);

    @Query("SELECT c FROM Comment c WHERE c.item.id = :itemId ORDER BY c.created DESC")
    List<Comment> findLatestByItemId(Long itemId);
}

