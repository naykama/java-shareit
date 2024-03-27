package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DirtiesContext
    public void findByItemIdInTest() {
        Set<Long> itemIds = new HashSet<>();
        User owner = new User(1L, "owner1@mail.ru", "owner1");
        User author = new User(2L, "author1@mail.ru", "author1");
        Item item = new Item(1L, "item1", "desc", true, owner.getId());
        Comment comment = new Comment("comment1", LocalDateTime.now(), item, author);

        assertEquals(0, commentRepository.findByItemIdIn(itemIds).size());

        userRepository.save(owner);
        userRepository.save(author);
        itemRepository.save(item);
        commentRepository.save(comment);

        itemIds.add(item.getId());
        assertEquals(1, commentRepository.findByItemIdIn(itemIds).size());
    }
}
