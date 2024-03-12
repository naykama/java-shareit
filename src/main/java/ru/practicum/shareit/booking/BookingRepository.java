package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {
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

    @Query("SELECT booking from Booking as booking join booking.item as itm join booking.booker as u where itm.id = ?1 AND u.id = ?2 " +
            "AND booking.status = 'APPROVED' AND booking.startDate <= ?3")
    List<Booking> findForCheckComment(long itemId, long bookerId, LocalDateTime commentDate);
}
