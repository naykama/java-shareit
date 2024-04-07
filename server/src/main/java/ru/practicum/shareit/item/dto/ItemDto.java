package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ItemDto {
    private final long id;
    private final String name;
    private final String description;
    @JsonProperty("available")
    private final Boolean isAvailableToRent;
    private Long requestId;
}
