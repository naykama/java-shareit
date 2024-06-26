package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "available_for_rent")
    private boolean isAvailableToRent;
    @Column(name = "owner_id")
    private Long ownerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    public Item(Long id, String name, String description, boolean isAvailableToRent, Long ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailableToRent = isAvailableToRent;
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        return id != null && id.equals(((Item) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
