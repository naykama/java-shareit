package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.utils.CustomPage.getPage;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    private ItemRequestService service;
    private static final Integer FROM = 0;
    private static final Integer SIZE = 2;
    private static final Pageable PAGE_CONFIG = getPage(FROM, SIZE, Sort.by("createDate"));
    private static final LocalDateTime CREATE_DAY = LocalDateTime.now();
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        service = new ItemRequestServiceImpl(requestRepository, userRepository, itemRepository);
    }

    @Test
    public void createRequestTest() {
        User author = createAuthor();
        ItemRequest request = createRequest();
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(requestRepository.save(request)).thenReturn(request);
        assertEquals(1L, service.createRequest(request).getId());
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    public void findAllRequestsTest() {
        User author = createAuthor();
        ItemRequest request = createRequest();
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(itemRepository.findByRequestIdIsNotNull()).thenReturn(List.of(createItem()));
        when(requestRepository.findAll()).thenReturn(List.of(request));
        assertEquals(1, service.findAllRequests(author.getId()).size());
    }

    @Test
    public void findAllFromOthersRequestsTest() {
        ItemRequest request = createRequest();
        request.setAuthorId(5L);
        Item item = createItem();

        when(userRepository.findById(5L)).thenReturn(Optional.of(new User(5L, "other@mail.ru", "other")));
        when(requestRepository.findByAuthorIdNot(5L, PAGE_CONFIG)).thenReturn((List.of(request)));
        when(itemRepository.findByRequestIdIsNotNull()).thenReturn(List.of(item));
        assertEquals(1, service.findAllFromOthersRequests(FROM, SIZE, 5L).size());
    }

    @Test
    public void findByIdTest() {
        User author = createAuthor();
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        assertThrows(NotFoundException.class, () -> service.findById(1L, author.getId()).getId());
        ItemRequest request = createRequest();
        when(itemRepository.findByRequestId(request.getId())).thenReturn(List.of(createItem()));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
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
