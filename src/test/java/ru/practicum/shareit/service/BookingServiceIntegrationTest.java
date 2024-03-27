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
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.item.dto.ItemMapper.convertToEntity;
import static ru.practicum.shareit.utils.CustomPage.getPage;

@SpringBootTest
public class BookingServiceIntegrationTest {
    private static final LocalDateTime START_DAY = LocalDateTime.now();
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Test
    @DirtiesContext
    public void createBookingTest() {
        Booking booking = createBooking();
        assertEquals(booking.getBooker().getEmail(),
                bookingService.findBookingById(1L, 1L).getBooker().getEmail());
    }

    @Test
    @DirtiesContext
    public void responseBookingTest() {
        Booking booking = createBooking();
        assertEquals(Booking.StatusType.APPROVED,
                bookingService.responseBooking(createOwner().getId(), booking.getId(), true).getStatus());
        booking.setStatus(Booking.StatusType.WAITING);
    }

    @Test
    @DirtiesContext
    public void findBookingByIdTest() {
        Booking booking = createBooking();
        assertEquals(booking.getId(), bookingService.findBookingById(createOwner().getId(), booking.getId()).getBookingId());
        assertEquals(booking.getId(), bookingService.findBookingById(createBooker().getId(), booking.getId()).getBookingId());
    }

    @Test
    @DirtiesContext
    public void findBookingForCurrentUserTest() {
        Booking firstBooking = createBooking();
        Integer from = 0;
        Integer size = 2;
        Pageable pageConfig = getPage(from, size, Sort.by("startDate").descending());

        assertEquals(1,
            bookingService.findBookingForCurrentUser(firstBooking.getBooker().getId(), "ALL", from, size).size());

        assertEquals(1,
            bookingService.findBookingForCurrentUser(firstBooking.getBooker().getId(), "CURRENT", from, size).size());

        assertThrows(RuntimeException.class, () ->
                bookingService.findBookingForCurrentUser(5L, "CURRENT", from, size).size());
    }

    @Test
    @DirtiesContext
    public void findBookingForOwnerTest() {
        Booking firstBooking = createBooking();
        Integer from = 0;
        Integer size = 2;
        Pageable pageConfig = getPage(from, size, Sort.by("startDate").descending());

        assertEquals(1,
                bookingService.findBookingForOwner(firstBooking.getItem().getOwnerId(), "ALL", from, size).size());

        assertEquals(1,
                bookingService.findBookingForOwner(firstBooking.getItem().getOwnerId(), "CURRENT", from, size).size());

        assertThrows(RuntimeException.class, () ->
                bookingService.findBookingForOwner(firstBooking.getBooker().getId(), "CURRENT", from, size).size());
    }

    private User createBooker() {
        User booker = new User(2L, "booker@mail.ru", "booker");
        return userService.createUser(booker);
    }

    private User createOwner() {
        User owner = new User(1L, "owner1@mail.ru", "owner1");
        return userService.createUser(owner);
    }

    private Item createItem() {
        User owner = createOwner();
        Item item = new Item(1L, "item1", "descr", true, owner.getId());
        return convertToEntity(itemService.createItem(new ItemDto(item.getId(), item.getName(), item.getDescription(),
                        true), owner.getId()), owner.getId(), null);
    }

    private Booking createBooking() {
        Item item = createItem();
        User booker = createBooker();
        Booking booking = new Booking(START_DAY, START_DAY.plusDays(5), item, booker);
        booking.setId(bookingService.createBooking(new BookingDto(item.getId(), booking.getStartDate(), booking.getEndDate()),
                booker.getId()).getBookingId());
        return booking;
    }

}
