package ru.practicum.shareit.repository1;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    private Pageable pageConfig = getPage(0, 2);
//    @Autowired
    private final UserRepository userRepository;
//    @Autowired
    private final ItemRepository itemRepository;
//    @Autowired
    private final ItemRequestRepository requestRepository;

    @Test
    public void getByOwnerIdTest() {
        Map<String, Long> ids = createContext();
        assertEquals(2, itemRepository.getByOwnerId(ids.get("ownerId"), pageConfig).size());
    }

    @Test
    public void searchTest() {
        createContext();
        String text = "descr";
        assertEquals(2,
                itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableToRentIsTrue(text,
                        text, pageConfig).size());
        String text1 = "item1";
        assertEquals(1,
                itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableToRentIsTrue(text1,
                        text1, pageConfig).size());
    }

    @Test
    public void findByRequestIdIsNotNullTest() {
        createContext();
        assertEquals(1, itemRepository.findByRequestIdIsNotNull().size());
    }

    @Test
    public void findByRequestId() {
        Map<String, Long> ids = createContext();
        assertEquals(1, itemRepository.findByRequestId(ids.get("requestId")).size());
    }


    private Map<String, Long> createContext() {
        Map<String, Long> allIds = new HashMap<>();
        long ownerId = userRepository.save(new User(1L, "owner@mail.ru", "Owner")).getId();
        long item1Id = itemRepository.save(new Item(1L, "item1", "descr1", true,
                ownerId)).getId();
        long authorId = userRepository.save(new User(2L, "author@mail.ru", "Owner")).getId();
        ItemRequest request = requestRepository.save(new ItemRequest(authorId, "for item2", LocalDateTime.now()));
        Item item2 = new Item(2L, "item2", "descr", true, ownerId);
        item2.setRequest(request);
        long item2Id = itemRepository.save(item2).getId();

        allIds.put("ownerId", ownerId);
        allIds.put("item1Id", item1Id);
        allIds.put("item2Id", item2Id);
        allIds.put("authorId", authorId);
        allIds.put("requestId", request.getId());
        return allIds;
    }
}
