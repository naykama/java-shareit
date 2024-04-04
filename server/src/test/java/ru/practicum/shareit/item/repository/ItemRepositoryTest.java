package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.utils.CustomPage.getPage;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemRepositoryTest {
    private Map<String, Long> ids;
    private Pageable pageConfig = getPage(0, 2);
    //    @Autowired
    private final UserRepository userRepository;
    //    @Autowired
    private final ItemRepository itemRepository;
    //    @Autowired
    private final ItemRequestRepository requestRepository;

    @Test
    public void getByOwnerIdTest() {
        assertEquals(1, itemRepository.getByOwnerId(ids.get("ownerId"), pageConfig).size());
    }

    @Test
    public void searchTest() {
        String text = "descr";
        System.out.println("allIds: " + ids);
        assertEquals(1,
                itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableToRentIsTrue(text,
                        text, pageConfig).size());
        String text1 = "item1";
        assertEquals(1,
                itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableToRentIsTrue(text1,
                        text1, pageConfig).size());
    }

    @Test
    public void findByRequestIdIsNotNullTest() {
        assertEquals(1, itemRepository.findByRequestIdIsNotNull().size());
    }

    @Test
    public void findByRequestId() {
        assertEquals(1, itemRepository.findByRequestId(ids.get("requestId")).size());
    }

    @BeforeAll
    public void createContext() {
        Map<String, Long> allIds = new HashMap<>();
        long ownerId = userRepository.save(new User(1L, "owner@mail.ru", "Owner")).getId();
        Item item1 = new Item(1L, "item1", "descr1", true, ownerId);
        long authorId = userRepository.save(new User(2L, "author@mail.ru", "Owner")).getId();
        ItemRequest request = requestRepository.save(new ItemRequest(authorId, "for item2", LocalDateTime.now()));
        item1.setRequest(request);
        long item1Id = itemRepository.save(item1).getId();
//        Item item2 = new Item(2L, "item2", "descr", true, ownerId);
//        long item2Id = itemRepository.save(item2).getId();

        allIds.put("ownerId", ownerId);
        allIds.put("item1Id", item1Id);
//        allIds.put("item2Id", item2Id);
        allIds.put("authorId", authorId);
        allIds.put("requestId", request.getId());
        ids = allIds;
    }

    @AfterAll
    public void deleteContext() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }
}
