package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemRequestServiceIntegrationTest {
    private static final Integer FROM = 0;
    private static final Integer SIZE = 2;
    private static final LocalDateTime CREATE_DAY = LocalDateTime.now();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestService service;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DirtiesContext
    public void createRequestTest() {
        User author = createAuthor();
        userRepository.save(author);
        ItemRequest request = createRequest();
        assertEquals(1L, service.createRequest(request).getId());
    }

    @Test
    @DirtiesContext
    public void findAllRequestsTest() {
        User author = createAuthor();
        ItemRequest request = createRequest();
        userRepository.save(author);
        service.createRequest(request);

        assertEquals(1, service.findAllRequests(author.getId()).size());
    }

    @Test
    @DirtiesContext
    public void findAllFromOthersRequestsTest() {
        userRepository.save(createAuthor());
        User otherUser = createAuthor();
        otherUser.setEmail("other@mail.ru");
        otherUser.setId(otherUser.getId() + 1);
        userRepository.save(otherUser);
        System.out.println("OtherUser id = " + otherUser.getId());
        ItemRequest request = createRequest();
        service.createRequest(request);

        assertEquals(1, service.findAllFromOthersRequests(FROM, SIZE, otherUser.getId()).size());
        assertEquals(0, service.findAllFromOthersRequests(FROM, SIZE, createAuthor().getId()).size());
    }

    @Test
    @DirtiesContext
    public void findByIdTest() {
        User author = createAuthor();
        ItemRequest request = createRequest();
        userRepository.save(author);
        service.createRequest(request);

        assertEquals(1L, service.findById(request.getId(), author.getId()).getId());
    }

    private User createAuthor() {
        return new User(1L, "owner1@mail.ru", "owner1");
    }

    private ItemRequest createRequest() {
        ItemRequest itemRequest = new ItemRequest(createAuthor().getId(), "descr", CREATE_DAY);
        itemRequest.setId(1L);
        return itemRequest;
    }

    private Item createItem() {
        Item item = new Item(1L, "item1", "descr", true, 1L);
        item.setRequest(createRequest());
        return item;
    }

}
