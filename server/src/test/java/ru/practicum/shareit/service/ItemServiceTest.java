package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.dto.ItemMapper.convertToEntity;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    private ItemService service;
    private static final Integer FROM = 0;
    private static final Integer SIZE = 2;
    private static final LocalDateTime CREATE_DAY = LocalDateTime.now();
    private static final LocalDateTime START_DAY = LocalDateTime.now();
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;

    @BeforeEach
    public void setUp() {
        service = new ItemServiceImpl(userRepository, itemRepository, bookingRepository, commentRepository, requestRepository);
    }

    @Test
    public void createItemTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createAuthor()));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(createRequest()));
        ItemDto dto = new ItemDto(1L, "item1", "descr", true);
        dto.setRequestId(1L);
        when(itemRepository.save(convertToEntity(dto, 1L, createRequest()))).thenReturn(createItem());

        assertEquals(1L, service.createItem(dto, 1L).getId());
    }

    @Test
    public void findAllItemsForOwnerTest() {
        Item item = createItem();
        Booking booking = createBooking();
        when(itemRepository.getByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(item));
        when(bookingRepository.findByItemIdInAndStatusNot(anyList(), any(Booking.StatusType.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItemIdIn(anySet())).thenReturn(List.of(createComment()));
        assertEquals(1, service.findAllItemsForOwner(1L, FROM, SIZE).size());
    }

    @Test
    public void findItemByIdTest() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createItem()));
        when(commentRepository.findByItemIdIn(anySet())).thenReturn(List.of(createComment()));
        when(bookingRepository.findByItemIdInAndStatusNot(anyList(), any(Booking.StatusType.class)))
                .thenReturn(List.of(createBooking()));
        assertEquals(1L, service.findItemById(1L, 1L).getId());
        assertEquals(1L, service.findItemById(1L, 2L).getId());
    }

    @Test
    public void updateItemByNotAuthorTest() {
        Item item = createItem();
        User notAuthor = createBooker();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(notAuthor));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> service.updateItem(item.getId(), new HashMap<>(), notAuthor.getId()));
    }

    @Test
    public void updateItemTest() {
        Item item = createItem();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createAuthor()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        Map<String, String> updatedParams = new HashMap<>();
        updatedParams.put("name", "newName");
        updatedParams.put("description", "newDesc");
        updatedParams.put("available", Boolean.toString(false));

        assertEquals("newName",service.updateItem(item.getId(), updatedParams, createAuthor().getId()).getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void searchItemsTest() {
        when(itemRepository
            .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableToRentIsTrue(anyString(),
                    anyString(), any(Pageable.class))).thenReturn(List.of(createItem()));
        assertEquals(0, service.searchItems("", FROM, SIZE).size());
        assertEquals(1, service.searchItems("item", FROM, SIZE).size());
    }

    @Test
    public void createCommentWithExceptionsTest() {
        assertThrows(IllegalArgumentException.class,() -> service.createComment("", CREATE_DAY, 1L, 1L));
        when(bookingRepository.findForCheckComment(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        assertThrows(IllegalArgumentException.class,() -> service.createComment("text", CREATE_DAY, 1L, 1L));
        when(bookingRepository.findForCheckComment(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(createBooking()));
        assertThrows(NotFoundException.class,() -> service.createComment("text", CREATE_DAY, 1L, 1L));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createAuthor()));
        assertThrows(NotFoundException.class,() -> service.createComment("text", CREATE_DAY, 1L, 1L));
    }

    @Test
    public void createCommentTest() {
        when(bookingRepository.findForCheckComment(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(createBooking()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createAuthor()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(createItem()));
        when(commentRepository.save(any(Comment.class))).thenReturn(createComment());
        assertEquals(createComment().getId(), service.createComment("text", CREATE_DAY, 1L, 1L).getId());
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
        Item item = new Item(createAuthor().getId(), "item1", "descr", true, 1L);
        item.setRequest(createRequest());
        return item;
    }

    private Booking createBooking() {
        Booking booking = new Booking(START_DAY, START_DAY.plusDays(5), createItem(), createBooker());
        booking.setId(1L);
        return booking;
    }

    private Comment createComment() {
        Comment comment = new Comment("text", CREATE_DAY, createItem(), createAuthor());
        comment.setId(1L);
        return comment;
    }

    private User createBooker() {
        return new User(2L, "booker@mail.ru", "booker");
    }

}
