package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long authorId;
    private String description;
    @Column(name = "create_date")
    private LocalDateTime createDate;

    public ItemRequest(long authorId, String description, LocalDateTime createDate) {
        this.authorId = authorId;
        this.description = description;
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemRequest)) return false;
        return id != null && id.equals(((ItemRequest) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
