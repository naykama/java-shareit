package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.utils.CustomPage.getPage;

@SpringBootTest
public class BookingServiceIntegrationTest {
    private static final LocalDateTime START_DAY = LocalDateTime.now();
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DirtiesContext
    public void createBookingTest() {
        Booking booking = createBooking();
        BookingDto bookingDto = new BookingDto(booking.getItem().getId(), booking.getStartDate(), booking.getEndDate());
        assertEquals(booking.getBooker().getEmail(),
                bookingService.createBooking(bookingDto, booking.getBooker().getId()).getBooker().getEmail());
    }

    @Test
    @DirtiesContext
    public void createBookingByOwnerTest() {
        Booking booking = createBooking();
        booking.getBooker().setId(booking.getItem().getOwnerId());
        assertThrows(NotFoundException.class,() -> bookingService.createBooking(new BookingDto(booking.getItem().getId(),
                        booking.getStartDate(), booking.getEndDate()), booking.getBooker().getId()));
    }

    @Test
    @DirtiesContext
    public void createBookingByNotUserTest() {
        Item item = createItem();
        itemRepository.save(item);
        User booker = createBooker();
        Booking booking = new Booking(START_DAY, START_DAY.plusDays(5), item, booker);
        assertThrows(NotFoundException.class,() -> bookingService.createBooking(new BookingDto(booking.getItem().getId(),
                booking.getStartDate(), booking.getEndDate()), booking.getBooker().getId()));
    }

    @Test
    @DirtiesContext
    public void responseBookingTest() {
        Booking booking = createBooking();
        bookingService.createBooking(new BookingDto(booking.getItem().getId(), booking.getStartDate(), booking.getEndDate()),
                booking.getBooker().getId());
        assertEquals(Booking.StatusType.APPROVED,
                bookingService.responseBooking(createOwner().getId(), booking.getId(), true).getStatus());
        booking.setStatus(Booking.StatusType.WAITING);
    }

    @Test
    @DirtiesContext
    public void responseBookingByNotOwnerTest() {
        Booking booking = createBooking();
        bookingService.createBooking(new BookingDto(booking.getItem().getId(), booking.getStartDate(), booking.getEndDate()),
                booking.getBooker().getId());
        assertThrows(NotFoundException.class, () -> bookingService.responseBooking(booking.getBooker().getId(),
                        booking.getId(), true).getStatus());
    }

    @Test
    @DirtiesContext
    public void findBookingByIdTest() {
        Booking booking = createBooking();
        bookingService.createBooking(new BookingDto(booking.getItem().getId(), booking.getStartDate(), booking.getEndDate()),
                booking.getBooker().getId());
        assertEquals(booking.getId(), bookingService.findBookingById(createOwner().getId(), booking.getId()).getBookingId());
        assertEquals(booking.getId(), bookingService.findBookingById(createBooker().getId(), booking.getId()).getBookingId());
    }

    @Test
    @DirtiesContext
    public void findBookingByNotIdTest() {
        assertThrows(NotFoundException.class, () -> bookingService.findBookingById(5, 5L));
    }

    @Test
    @DirtiesContext
    public void findBookingForCurrentUserTest() {
        Booking firstBooking = createBooking();
        bookingService.createBooking(new BookingDto(firstBooking.getItem().getId(), firstBooking.getStartDate(), firstBooking.getEndDate()),
                firstBooking.getBooker().getId());
        Integer from = 0;
        Integer size = 2;
        Pageable pageConfig = getPage(from, size, Sort.by("startDate").descending());

        assertEquals(1,
            bookingService.findBookingForCurrentUser(firstBooking.getBooker().getId(), "ALL", from, size).size());

        assertEquals(1,
            bookingService.findBookingForCurrentUser(firstBooking.getBooker().getId(), "CURRENT", from, size).size());

        assertThrows(RuntimeException.class, () ->
                bookingService.findBookingForCurrentUser(5L, "CURRENT", from, size).size());
        assertThrows(RuntimeException.class, () ->
                bookingService.findBookingForCurrentUser(firstBooking.getBooker().getId(), "FUTURE", from, size).size());
        assertEquals(1,
                bookingService.findBookingForCurrentUser(firstBooking.getBooker().getId(), "WAITING", from, size).size());
    }

    @Test
    public void checkBookingMapperTest() {
        assertEquals(ItemMapper.class, new ItemMapper().getClass());
        assertEquals(CommentMapper.class, new CommentMapper().getClass());
        assertEquals(UserMapper.class, new UserMapper().getClass());
        assertEquals(BookingMapper.class, new BookingMapper().getClass());
        assertNull(BookingMapper.convertToDtoWithoutItem(null));
    }

    @Test
    @DirtiesContext
    public void findBookingForOwnerTest() {
        Booking firstBooking = createBooking();
        bookingService.createBooking(new BookingDto(firstBooking.getItem().getId(), firstBooking.getStartDate(), firstBooking.getEndDate()),
                firstBooking.getBooker().getId());
        Integer from = 0;
        Integer size = 2;
        Pageable pageConfig = getPage(from, size, Sort.by("startDate").descending());

        assertEquals(1,
                bookingService.findBookingForOwner(firstBooking.getItem().getOwnerId(), "ALL", from, size).size());

        assertEquals(1,
                bookingService.findBookingForOwner(firstBooking.getItem().getOwnerId(), "CURRENT", from, size).size());

        assertThrows(RuntimeException.class, () ->
                bookingService.findBookingForOwner(firstBooking.getItem().getOwnerId(), "FUTURE", from, size).size());
        assertEquals(1,
                bookingService.findBookingForOwner(firstBooking.getItem().getOwnerId(), "WAITING", from, size).size());
        assertThrows(RuntimeException.class, () ->
                bookingService.findBookingForOwner(firstBooking.getBooker().getId(), "CURRENT", from, size).size());
    }

    private User createBooker() {
        User booker = new User(2L, "booker@mail.ru", "booker");
        return booker;
    }

    private User createOwner() {
        User owner = new User(1L, "owner1@mail.ru", "owner1");
        return owner;
    }

    private Item createItem() {
        User owner = createOwner();
        userRepository.save(owner);
        Item item = new Item(1L, "item1", "descr", true, owner.getId());;
        return item;
    }

    private Booking createBooking() {
        Item item = createItem();
        User booker = createBooker();
        itemRepository.save(item);
        userRepository.save(booker);
        Booking booking = new Booking(START_DAY, START_DAY.plusDays(5), item, booker);
        booking.setId(1L);
        return booking;
    }

}
