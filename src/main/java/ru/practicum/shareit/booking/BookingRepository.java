package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.List;

//@Repository
public interface BookingRepository extends CrudRepository<Booking, Long> {
    List<Booking> findByItemId(long itemId);

    List<Booking> findByBookerId(long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndEndDateBefore(long bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfter(long bookerId, LocalDateTime date1, LocalDateTime date2,
                                                                  Pageable pageable);

    List<Booking> findByBookerIdAndStartDateAfter(long bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(long bookerId, Booking.StatusType status, Pageable pageable);

    List<Booking> findByItemOwnerId(long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndDateBefore(long bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(long bookerId, LocalDateTime date1,
                                                                     LocalDateTime date2, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartDateAfter(long bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(long bookerId, Booking.StatusType status, Pageable pageable);

    @Query("SELECT booking from Booking as booking\n" +
            "JOIN FETCH booking.item as itm\n" +
            "JOIN FETCH booking.booker as u\n" +
            "WHERE itm.id = ?1 AND u.id = ?2 AND booking.status = 'APPROVED' AND booking.startDate <= ?3")
    List<Booking> findForCheckComment(long itemId, long bookerId, LocalDateTime commentDate);

    List<Booking> findByItemIdInAndStatusNot(List<Long> itemIds, Booking.StatusType status);
}
