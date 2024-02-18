package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class Item {
    private long id;
    @NotEmpty(message = "name cannot be empty")
    private String name;
    @NotEmpty(message = "description cannot be empty")
    private String description;
    private boolean isAvailableToRent;
    private long ownerId;
}
