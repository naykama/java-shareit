package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.Comment;

import java.util.List;
import java.util.Set;

//@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItemIdIn(Set<Long> itemIds);
}
