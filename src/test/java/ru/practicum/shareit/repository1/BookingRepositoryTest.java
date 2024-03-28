package ru.practicum.shareit.repository1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.utils.CustomPage.getPage;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private void createFirstBooking() {
        User owner = new User(1L, "owner1@mail.ru", "owner1");
        User booker = new User(2L, "booker1@mail.ru", "booker1");
        Item item = new Item(1L, "item1", "desc", true, owner.getId());
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(5L), item, booker);
        bookingRepository.save(booking);
    }

    private void createSecondBooking() {
        Item item = itemRepository.getById(1L);
        User booker = new User(3L, "booker2@mail.ru", "booker2");
        userRepository.save(booker);
        Booking booking = new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(5L), item, booker);
        bookingRepository.save(booking);
    }

    /**
     * Ids description:
     * Item with id=1 has owner (user with id = 1), and two bookings (ids:1, 2) (user with id = 2 and user with id = 3)
     */

    @Test
    @DirtiesContext
    public void findByItemIdTest() {
        List<Booking> bookings = bookingRepository.findByItemId(1);
        assertEquals(0, bookings.size());

        createFirstBooking();
        bookings = bookingRepository.findByItemId(1);
        assertEquals(1, bookings.size());

        createSecondBooking();
        bookings = bookingRepository.findByItemId(1);
        assertEquals(2, bookings.size());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdTest() {
        List<Booking> bookings = bookingRepository.findByBookerId(1L, getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        createFirstBooking();
        createSecondBooking();
        bookings = bookingRepository.findByBookerId(2L, getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdAndEndDateBeforeTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndDateBefore(3L,
                LocalDateTime.now().plusDays(7), getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        createFirstBooking();
        createSecondBooking();
        bookings = bookingRepository.findByBookerId(3L, getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(2, bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdAndStartDateBeforeAndEndDateAfterTest() {
        createFirstBooking();
        createSecondBooking();

        List<Booking> bookings = bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(2L,
                LocalDateTime.now().minusDays(5), LocalDateTime.now(),
                getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());


        bookings = bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(2L,
                LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3),
                getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdAndStartDateAfterTest() {
        createFirstBooking();
        createSecondBooking();

        List<Booking> bookings = bookingRepository.findByBookerIdAndStartDateAfter(2L, LocalDateTime.now().plusDays(2),
                getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        bookings = bookingRepository.findByBookerIdAndStartDateAfter(2L, LocalDateTime.now().minusDays(2),
                getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdAndStatusTest() {
        createFirstBooking();
        createSecondBooking();

        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(2L, Booking.StatusType.REJECTED,
                getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        bookings = bookingRepository.findByBookerIdAndStatus(2L, Booking.StatusType.WAITING,
                getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByItemOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findByItemOwnerId(1L, getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        createFirstBooking();
        createSecondBooking();

        bookings = bookingRepository.findByItemOwnerId(1L, getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(2, bookings.size());
        assertEquals(2, bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByItemOwnerIdAndEndDateBeforeTest() {
        List<Booking> bookings = bookingRepository.findByBookerId(1L, getPage(4, 2,
                Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        createFirstBooking();
        createSecondBooking();
        bookings = bookingRepository.findByBookerId(2L, getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findForCheckCommentTest() {
        createFirstBooking();
        createSecondBooking();
        Booking booking = bookingRepository.findById(1L).orElseThrow(() -> new NotFoundException(
                String.format("Booking with id 1 not found")));
        booking.setStatus(Booking.StatusType.APPROVED);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findForCheckComment(1, 2, LocalDateTime.now().plusDays(3));
        assertEquals(1, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByItemIdInAndStatusNotTest() {
        createFirstBooking();
        createSecondBooking();

        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusNot(Arrays.asList(1L, 2L), Booking.StatusType.WAITING);
        assertEquals(0, bookings.size());

        bookings = bookingRepository.findByItemIdInAndStatusNot(Arrays.asList(1L, 2L), Booking.StatusType.APPROVED);
        assertEquals(2, bookings.size());
        assertEquals(1, bookings.get(0).getId());
    }
}
