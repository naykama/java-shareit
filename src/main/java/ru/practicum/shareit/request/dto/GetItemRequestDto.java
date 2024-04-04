package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GetItemRequestDto extends ItemRequestDto {
    private final List<ItemDto> items;

    public GetItemRequestDto(Long id, long authorId, String description, LocalDateTime createDate, List<ItemDto> items) {
        super(id, authorId, description, createDate);
        this.items = items;
    }
}
