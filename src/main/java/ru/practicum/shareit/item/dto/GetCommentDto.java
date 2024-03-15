package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
public class GetCommentDto {
    private final Long id;
    private final String text;
    private final String authorName;
    @JsonProperty("created")
    private final LocalDateTime createDate;
}
