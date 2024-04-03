package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
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

    private Booking createFirstBooking() {
        User owner = userRepository.save(new User(1L, "owner1@mail.ru", "owner1"));
        User booker = userRepository.save(new User(2L, "booker1@mail.ru", "booker1"));
        Item item = itemRepository.save(new Item(1L, "item1", "desc", true, owner.getId()));
        return bookingRepository.save(new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(5L), item, booker));
    }

    private Booking createSecondBooking() {
        Item item = itemRepository.findAll().get(0);
        User booker = userRepository.save(new User(3L, "booker2@mail.ru", "booker2"));
        return bookingRepository.save(new Booking(LocalDateTime.now(), LocalDateTime.now().plusDays(5L), item, booker));
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

        Booking booking = createFirstBooking();
        bookings = bookingRepository.findByItemId(booking.getItem().getId());
        assertEquals(1, bookings.size());

        Booking booking2 = createSecondBooking();
        bookings = bookingRepository.findByItemId(booking2.getItem().getId());
        assertEquals(2, bookings.size());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdTest() {
        List<Booking> bookings = bookingRepository.findByBookerId(1L, getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        Booking booking = createFirstBooking();
        Booking booking2 = createSecondBooking();
        bookings = bookingRepository.findByBookerId(booking.getBooker().getId(), getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdAndEndDateBeforeTest() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndDateBefore(3L,
                LocalDateTime.now().plusDays(7), getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        Booking booking = createFirstBooking();
        Booking booking2 = createSecondBooking();
        bookings = bookingRepository.findByBookerId(booking2.getBooker().getId(), getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(booking2.getId(), bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdAndStartDateBeforeAndEndDateAfterTest() {
        Booking booking = createFirstBooking();
        Booking booking2 = createSecondBooking();
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(
                booking.getBooker().getId(),
                LocalDateTime.now().minusDays(5), LocalDateTime.now(),
                getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        bookings = bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(
                booking.getBooker().getId(),
                LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3),
                getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdAndStartDateAfterTest() {
        Booking booking = createFirstBooking();
        Booking booking2 = createSecondBooking();

        List<Booking> bookings = bookingRepository.findByBookerIdAndStartDateAfter(booking.getBooker().getId(),
                LocalDateTime.now().plusDays(2), getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        bookings = bookingRepository.findByBookerIdAndStartDateAfter(booking.getBooker().getId(),
                LocalDateTime.now().minusDays(2), getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByBookerIdAndStatusTest() {
        Booking booking = createFirstBooking();
        Booking booking2 = createSecondBooking();
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatus(booking.getBooker().getId(),
                Booking.StatusType.REJECTED, getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        bookings = bookingRepository.findByBookerIdAndStatus(booking.getBooker().getId(), Booking.StatusType.WAITING,
                getPage(0, 2, Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByItemOwnerIdTest() {
        List<Booking> bookings = bookingRepository.findByItemOwnerId(1L, getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        Booking booking = createFirstBooking();
        Booking booking2 = createSecondBooking();

        bookings = bookingRepository.findByItemOwnerId(booking2.getItem().getOwnerId(), getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(2, bookings.size());
        assertEquals(booking2.getId(), bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByItemOwnerIdAndEndDateBeforeTest() {
        List<Booking> bookings = bookingRepository.findByBookerId(1L, getPage(4, 2,
                Sort.by("startDate").descending()));
        assertEquals(0, bookings.size());

        Booking booking = createFirstBooking();
        Booking booking2 = createSecondBooking();
        bookings = bookingRepository.findByBookerId(booking.getBooker().getId(), getPage(0, 2,
                Sort.by("startDate").descending()));
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findForCheckCommentTest() {
        Booking booking = createFirstBooking();
        Booking booking2 = createSecondBooking();
        booking.setStatus(Booking.StatusType.APPROVED);
        bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findForCheckComment(booking.getItem().getId(), booking.getBooker().getId(),
                LocalDateTime.now().plusDays(3));
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void findByItemIdInAndStatusNotTest() {
        Booking booking = createFirstBooking();
        Booking booking2 = createSecondBooking();

        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusNot(
                Arrays.asList(booking.getItem().getId(), booking2.getItem().getId()),
                Booking.StatusType.WAITING);
        assertEquals(0, bookings.size());

        bookings = bookingRepository.findByItemIdInAndStatusNot(
                Arrays.asList(booking.getItem().getId(), booking2.getItem().getId()),
                Booking.StatusType.APPROVED);
        assertEquals(2, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }
}
