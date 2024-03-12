package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRepository;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.booking.Booking.StatusType;
import ru.practicum.shareit.item.dto.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.convertToEntity;
import static ru.practicum.shareit.booking.dto.BookingMapper.convertToGetDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public GetBookingDto createBooking(BookingDto bookingDto, long bookerId) {
        Item bookedItem = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException(
                                            String.format("Item with id = %d not found", bookingDto.getItemId())));
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException(
                                                            String.format("User with id = %d not found", bookerId)));
        if (!bookedItem.isAvailableToRent() || !checkDates(bookingDto)) {
            log.error("IllegalArgumentException in createBooking(). Check arguments");
            throw new IllegalArgumentException(String.format("Error in createBooking(). Check arguments"));
        }
        if (bookerId == bookedItem.getOwnerId()) {
            log.error("Owner can not book his item");
            throw new NotFoundException(String.format("Owner can not book his item"));
        }
        Booking booking = convertToEntity(bookingDto, bookedItem, booker);
        booking.setStatus(StatusType.WAITING);
        log.info("Booking with id = {} created", booking.getId());
        return convertToGetDto(bookingRepository.save(booking));
    }

    @Override
    public GetBookingDto responseBooking(long ownerId, long bookingId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                                            String.format("Booking with id = %d not found", bookingId)));
        if (booking.getItem().getOwnerId() != ownerId) {
            log.error("User with id = {} is not a owner of item in booking", ownerId);
            throw new NotFoundException(
                    String.format("User with id = %d is not a owner of item. Owner is a user with id = %d",
                            ownerId, booking.getItem().getOwnerId()));
        }
        if (booking.getStatus() == StatusType.WAITING) {
            booking.setStatus(isApproved ? StatusType.APPROVED : StatusType.REJECTED);
        } else {
            log.error("Booking with id = {} is already has status not WAITING", bookingId);
            throw new IllegalArgumentException(String.format("Booking with id = %d is already has status not WAITING", bookingId));
        }
        return convertToGetDto(bookingRepository.save(booking));
    }

    @Override
    public GetBookingDto findBookingById(long bookerOrOwnerId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(
                                            String.format("Booking with id = %d not found", bookingId)));
        if (bookerOrOwnerId != booking.getBooker().getId() && bookerOrOwnerId != booking.getItem().getOwnerId()) {
            log.error("User with id = {} is not a owner of item or booker", bookerOrOwnerId);
            throw new NotFoundException(String.format("User with id = %d is not a owner of item or booker",
                                                                                            bookerOrOwnerId));
        }
        return convertToGetDto(booking);
    }

    @Override
    public List<GetBookingDto> findBookingForCurrentUser(long bookerId, String state) {
        List<Booking> bookings = new ArrayList<>();
        try {
            switch (BookingStatus.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findByBookerId(bookerId);
                    break;
                case PAST:
                    bookings = bookingRepository.findByBookerIdAndEndDateBefore(bookerId, LocalDateTime.now());
                    break;
                case CURRENT:
                    bookings = bookingRepository.findByBookerIdAndStartDateBeforeAndEndDateAfter(bookerId, LocalDateTime.now(), LocalDateTime.now());
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByBookerIdAndStartDateAfter(bookerId, LocalDateTime.now());
                    break;
                case WAITING:
                    bookings = bookingRepository.findByBookerIdAndStatus(bookerId, StatusType.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByBookerIdAndStatus(bookerId, StatusType.REJECTED);
                    break;
            }
        } catch (IllegalArgumentException e) {
            log.error("Unknown state: {}", state);
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
        if (bookings.size() == 0) {
            log.error("Bookings for booker with id = {} not found", bookerId);
            throw new RuntimeException(String.format("Bookings for booker with id = %d not found", bookerId));
        }
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStartDate).reversed())
                .map(BookingMapper::convertToGetDto)
                .collect(Collectors.toList());
    }

    public List<GetBookingDto> findBookingForOwner(long ownerId, String state) {
        List<Booking> bookings = new ArrayList<>();
        try {
            switch (BookingStatus.valueOf(state)) {
                case ALL:
                    bookings = bookingRepository.findByItemOwnerId(ownerId);
                    break;
                case PAST:
                    bookings = bookingRepository.findByItemOwnerIdAndEndDateBefore(ownerId, LocalDateTime.now());
                    break;
                case CURRENT:
                    bookings = bookingRepository.findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(ownerId, LocalDateTime.now(), LocalDateTime.now());
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByItemOwnerIdAndStartDateAfter(ownerId, LocalDateTime.now());
                    break;
                case WAITING:
                    bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, StatusType.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, StatusType.REJECTED);
                    break;
            }
        } catch (IllegalArgumentException e) {
            log.error("Unknown state: {}", state);
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
        if (bookings.size() == 0) {
            log.error("Bookings for owner with id = {} not found", ownerId);
            throw new RuntimeException(String.format("Bookings for owner with id = %d not found", ownerId));
        }
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStartDate).reversed())
                .map(BookingMapper::convertToGetDto)
                .collect(Collectors.toList());
    }

    private boolean checkDates(BookingDto booking) {
        List<Booking> bookingsForItem = bookingRepository.findByItemId(booking.getItemId());
        LocalDateTime startDate = booking.getStartDate();
        LocalDateTime endDate = booking.getEndDate();
        for (Booking existBooking : bookingsForItem) {
            if (endDate.isBefore(existBooking.getStartDate()) || startDate.isAfter(existBooking.getEndDate())) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public enum BookingStatus {
        ALL, CURRENT, FUTURE, PAST, WAITING, REJECTED;
    }
}
