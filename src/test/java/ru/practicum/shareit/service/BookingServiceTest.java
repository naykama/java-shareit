package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.dto.BookingMapper.convertToEntity;
import static ru.practicum.shareit.utils.CustomPage.getPage;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    private BookingService service;
    private static final LocalDateTime TODAY = LocalDateTime.now();
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        service = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    public void createBookingByOwnerTest() {
        User bookerAndOwner = createOwner();
        Booking booking = createBooking();
        BookingDto bookingDto = new BookingDto(booking.getItem().getId(), booking.getStartDate(), booking.getEndDate());
        booking.setId(1L);

        assertThrows(NotFoundException.class, () -> service.createBooking(bookingDto, bookerAndOwner.getId()));
    }

    @Test
    public void createBookingFotNotAvailableItemTest() {
        Item item = createItem();
        item.setAvailableToRent(false);
        User booker = createBooker();
        Booking booking = createBooking();
        BookingDto bookingDto = new BookingDto(booking.getItem().getId(), booking.getStartDate(), booking.getEndDate());
        booking.setId(1L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));

        assertThrows(IllegalArgumentException.class, () -> service.createBooking(bookingDto, booker.getId()));
    }

    @Test
    public void createBookingTest() {
        Item item = createItem();
        User booker = createBooker();
        BookingDto bookingDto = new BookingDto(item.getId(), TODAY, TODAY.plusDays(5));
        Booking booking = convertToEntity(bookingDto, item, booker);

        Booking booking1 = new Booking(booking.getStartDate(), booking.getEndDate(), item, booker);
        booking1.setId(1L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(booking)).thenReturn(booking1);

        assertEquals(1L, service.createBooking(bookingDto, booker.getId()).getBookingId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    public void responseBookingByNotOwnerTest() {
        assertThrows(NotFoundException.class, () -> service.responseBooking(
                createBooker().getId(), createBooking().getId(), any(Boolean.class)));
    }

    @Test
    public void responseBookingWithStatusNotWaitingTest() {
        Booking booking = createBooking();
        booking.setStatus(Booking.StatusType.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> service.responseBooking(
                createOwner().getId(), booking.getId(), any(Boolean.class)));
    }

    @Test
    public void responseBookingTest() {
        Booking booking = createBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);
        assertEquals(Booking.StatusType.APPROVED,
                service.responseBooking(createOwner().getId(), booking.getId(), true).getStatus());
        booking.setStatus(Booking.StatusType.WAITING);
        assertEquals(Booking.StatusType.REJECTED,
                service.responseBooking(createOwner().getId(), booking.getId(), false).getStatus());
        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    public void findBookingByIdForNotOwnerOrBookerTest() {
        Booking booking = createBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> service.findBookingById(
                5, booking.getId()));
    }

    @Test
    public void findBookingByIdTest() {
        Booking booking = createBooking();
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertEquals(booking.getId(), service.findBookingById(createOwner().getId(), booking.getId()).getBookingId());
        assertEquals(booking.getId(), service.findBookingById(createBooker().getId(), booking.getId()).getBookingId());
    }

    @Test
    public void findBookingForCurrentUserTest() {
        Booking firstBooking = createBooking();
        Booking secondBooking = createSecondBooking();
        Integer from = 0;
        Integer size = 2;
        Pageable pageConfig = getPage(from, size, Sort.by("startDate").descending());

        when(bookingRepository.findByBookerId(firstBooking.getBooker().getId(), pageConfig))
                .thenReturn(Arrays.asList(firstBooking, secondBooking));
        when(bookingRepository.findByBookerIdAndEndDateBefore(anyLong(),
                any(LocalDateTime.class), any(Pageable.class))).thenReturn(Arrays.asList(secondBooking));

        assertEquals(2,
                service.findBookingForCurrentUser(firstBooking.getBooker().getId(), "ALL", from, size).size());

        assertEquals(1,
                service.findBookingForCurrentUser(secondBooking.getBooker().getId(), "PAST", from, size).size());
    }

    @Test
    public void findBookingForCurrentUserTestWithNotTrueUser() {
        Integer from = 0;
        Integer size = 2;
        assertThrows(RuntimeException.class, () ->
                service.findBookingForCurrentUser(5L, "REJECTED", from, size));
    }

    @Test
    public void findBookingForCurrentUserTestWithNotTrueState() {
        Integer from = 0;
        Integer size = 2;
        assertThrows(IllegalArgumentException.class, () ->
                service.findBookingForCurrentUser(5L, "WrongState", from, size));
    }

    private User createBooker() {
        return new User(2L, "booker@mail.ru", "booker");
    }

    private User createOwner() {
        return new User(1L, "owner1@mail.ru", "owner1");
    }

    private Item createItem() {
        return new Item(1L, "item1", "descr", true, 1L);
    }

    private Booking createBooking() {
        Booking booking = new Booking(TODAY, TODAY.plusDays(5), createItem(), createBooker());
        booking.setId(1L);
        return booking;
    }

    private Booking createSecondBooking() {
        Booking booking = new Booking(TODAY.minusDays(5), TODAY.minusDays(3), createItem(), createBooker());
        booking.setId(2L);
        return booking;
    }
}