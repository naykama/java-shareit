package ru.practicum.shareit.repository1;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void findByItemIdInTest() {
        Set<Long> itemIds = new HashSet<>();
        User owner = userRepository.save(new User(1L, "owner1@mail.ru", "owner1"));
        User author = userRepository.save(new User(2L, "author1@mail.ru", "author1"));
        Item item = itemRepository.save(new Item(1L, "item1", "desc", true, owner.getId()));
        Comment comment = new Comment("comment1", LocalDateTime.now(), item, author);

        assertEquals(0, commentRepository.findByItemIdIn(itemIds).size());
        System.out.println("UserRepository: " + userRepository.findAll());
        System.out.println("ItemRepository: " + itemRepository.findAll());
        itemRepository.save(item);
        commentRepository.save(comment);

        itemIds.add(item.getId());
        assertEquals(1, commentRepository.findByItemIdIn(itemIds).size());
    }

    @AfterAll
    public void deleteContext() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }
}
