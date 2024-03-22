package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;

public class ItemRequestMapping {
    public static ItemRequest convertToEntity(String description, long authorId, LocalDateTime createDate) {
        return new ItemRequest(authorId, description, createDate);
    }

    public static ItemRequestDto convertToDto(ItemRequest request) {
        return new ItemRequestDto(1L, request.getAuthorId(), request.getDescription(), request.getCreateDate());
    }
}
