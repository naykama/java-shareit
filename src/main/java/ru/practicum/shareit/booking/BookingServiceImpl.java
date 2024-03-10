package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRepository;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.booking.Booking.StatusType;
import ru.practicum.shareit.item.dto.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

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
        if (bookedItem.isAvailableToRent() && checkDates(bookingDto) && bookerId != bookedItem.getOwnerId()) {
            Booking booking = convertToEntity(bookingDto, bookerId, bookedItem, booker);
            booking.setStatus(StatusType.WAITING);
            log.info("Booking with id = {} created", booking.getId());
            return convertToGetDto(bookingRepository.save(booking));
        }
        log.error("IllegalArgumentException in createBooking(). Check arguments");
        throw new IllegalArgumentException(String.format("Error in createBooking(). Check arguments"));
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
}
