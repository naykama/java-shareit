package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;
    @Column(name = "start_date")
    @NotNull
    private LocalDateTime startDate;
    @Column(name = "end_date")
    @NotNull
    private LocalDateTime endDate;
    @Enumerated(EnumType.STRING)
    private StatusType status;

    public Booking(LocalDateTime startDate, LocalDateTime endDate, Item item, User booker) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.item = item;
        this.booker = booker;
    }

    public enum StatusType {
        WAITING, APPROVED, REJECTED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        return id != null && id.equals(((Booking) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
