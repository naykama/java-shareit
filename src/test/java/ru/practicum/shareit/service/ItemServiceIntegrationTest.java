package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ItemServiceIntegrationTest {
    private static final Integer FROM = 0;
    private static final Integer SIZE = 2;
    private static final LocalDateTime CREATE_DAY = LocalDateTime.now();
    private static final LocalDateTime START_DAY = LocalDateTime.now();
    @Autowired
    private ItemService service;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRequestRepository requestRepository;

    @Test
    @DirtiesContext
    public void createItemTest() {
        userRepository.save(createAuthor());
        requestRepository.save(createRequest());
        ItemDto dto = new ItemDto(1L, "item1", "descr", true);
        dto.setRequestId(1L);
        assertEquals(1L, service.createItem(dto, 1L).getId());
    }

    @Test
    @DirtiesContext
    public void findByNotValidIdTest() {
        userRepository.save(createAuthor());
        assertThrows(NotFoundException.class, () -> service.updateItem(1L, new HashMap<>(), 1L));
        assertThrows(NotFoundException.class, () -> service.findItemById(1L, 1L));
    }

    private User createAuthor() {
        return new User(1L, "owner1@mail.ru", "owner1");
    }

    private ItemRequest createRequest() {
        ItemRequest itemRequest = new ItemRequest(createAuthor().getId(), "descr", CREATE_DAY);
        itemRequest.setId(1L);
        return itemRequest;
    }
}
