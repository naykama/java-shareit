package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class RequestDto {
    private final Long id;
    private final Long authorId;
    @NotNull
    private final String description;
    @JsonProperty("created")
    private final LocalDateTime createDate;
}
