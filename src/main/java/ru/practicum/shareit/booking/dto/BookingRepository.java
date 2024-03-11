package ru.practicum.shareit.booking.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemId(long itemId);

    List<Booking> findByBookerId(long bookerId);

    List<Booking> findByBookerIdAndEndDateBefore(long bookerId, LocalDateTime date);

    List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfter(long bookerId, LocalDateTime date1, LocalDateTime date2);

    List<Booking> findByBookerIdAndStartDateAfter(long bookerId, LocalDateTime date);

    List<Booking> findByBookerIdAndStatus(long bookerId, Booking.StatusType status);

    List<Booking> findByItemOwnerId(long ownerId);

    List<Booking> findByItemOwnerIdAndEndDateBefore(long bookerId, LocalDateTime date);

    List<Booking> findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(long bookerId, LocalDateTime date1, LocalDateTime date2);

    List<Booking> findByItemOwnerIdAndStartDateAfter(long bookerId, LocalDateTime date);

    List<Booking> findByItemOwnerIdAndStatus(long bookerId, Booking.StatusType status);
}
