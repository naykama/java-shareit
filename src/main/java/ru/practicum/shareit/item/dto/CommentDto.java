package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CommentDto {
    @NotNull
    private String text;
}
