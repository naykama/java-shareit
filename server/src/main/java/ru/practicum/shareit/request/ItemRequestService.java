package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequest request);

    List<GetItemRequestDto> findAllRequests(long userId);

    List<GetItemRequestDto> findAllFromOthersRequests(int from, int size, long userId);

    GetItemRequestDto findById(long requestId, long userId);
}
