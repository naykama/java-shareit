package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ItemRequestDto {
    private final Long id;
    private final long authorId;
    private final String description;
    @JsonProperty("created")
    private final LocalDateTime createDate;

    public ItemRequestDto(String description) {
        id = null;
        authorId = 0;
        this.description = description;
        createDate = null;
    }
}
