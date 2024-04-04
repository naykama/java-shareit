package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class ItemDto {
    private final long id;
    @NotEmpty(message = "name cannot be empty")
    private final String name;
    @NotEmpty(message = "description cannot be empty")
    private final String description;
    @JsonProperty("available")
    @NotNull
    private final Boolean isAvailableToRent;
    private Long requestId;
}
