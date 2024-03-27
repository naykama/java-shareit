package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapping {
    public static ItemRequest convertToEntity(String description, long authorId, LocalDateTime createDate) {
        return new ItemRequest(authorId, description, createDate);
    }

    public static ItemRequestDto convertToDto(ItemRequest request) {
        return new ItemRequestDto(request.getId(), request.getAuthorId(), request.getDescription(), request.getCreateDate());
    }

    public static GetItemRequestDto convertToGetDto(ItemRequest request, List<Item> items) {
        return new GetItemRequestDto(request.getId(), request.getAuthorId(), request.getDescription(), request.getCreateDate(),
                items.stream()
                        .map(ItemMapper::convertToDto)
                        .collect(Collectors.toList()));
    }
}
