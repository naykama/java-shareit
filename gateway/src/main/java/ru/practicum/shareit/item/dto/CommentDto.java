package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Data
public class CommentDto {
    private final Long id;
    @NotNull
    private final String text;
    private final String authorName;
    @JsonProperty("created")
    private final LocalDateTime createDate;
}
