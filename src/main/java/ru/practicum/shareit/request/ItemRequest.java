package ru.practicum.shareit.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    private int id;
    @NotNull
    private String itemName;
    private final List<Long> offerOwnerIds;
}
