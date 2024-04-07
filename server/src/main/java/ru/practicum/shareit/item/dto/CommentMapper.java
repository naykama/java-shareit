package ru.practicum.shareit.item.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Slf4j
public class CommentMapper {
    public static GetCommentDto convertToGetDto(Comment comment) {
        return new GetCommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreateDate());
    }

    public static Comment convertToEntity(String text, LocalDateTime date, User author, Item item) {
        log.debug("convertToEntity: comment");
        return new Comment(text, date, item, author);
    }
}
