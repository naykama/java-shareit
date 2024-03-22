package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
public class PostItemRequestDto {
    @NotNull
    @NotEmpty
    private String description;
}
