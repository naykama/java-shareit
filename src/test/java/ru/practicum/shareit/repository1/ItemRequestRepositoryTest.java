package ru.practicum.shareit.repository1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.utils.CustomPage.getPage;

;

@DataJpaTest
public class ItemRequestRepositoryTest {
    private Pageable pageConfig = getPage(0, 2);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    @DirtiesContext
    public void findByAuthorIdNotTest() {
        Map<String, Long> allIds = createContext();
        assertEquals(1, requestRepository.findByAuthorIdNot(allIds.get("request1Id"), pageConfig).size());
    }

    private Map<String, Long> createContext() {
        Map<String, Long> allIds = new HashMap<>();
        long author1Id = userRepository.save(new User(1L, "owner@mail.ru", "Owner")).getId();
        long author2Id = userRepository.save(new User(2L, "author@mail.ru", "Owner")).getId();
        long request1Id = requestRepository.save(new ItemRequest(author1Id, "for item2", LocalDateTime.now())).getId();
        long request2Id = requestRepository.save(new ItemRequest(author2Id, "for item2", LocalDateTime.now())).getId();
        allIds.put("author1Id", author1Id);
        allIds.put("author2Id", author2Id);
        allIds.put("request1Id", request1Id);
        allIds.put("request2Id", request2Id);
        return allIds;
    }
}
