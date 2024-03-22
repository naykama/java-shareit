package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapping;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.ItemRequestMapping.convertToDto;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequest request) {
        findUserForRequest(request.getAuthorId());
        return convertToDto(requestRepository.save(request));
    }

    @Override
    public List<GetItemRequestDto> findAllRequests(long userId) {
        findUserForRequest(userId);
        List<ItemRequest> itemRequests = requestRepository.findAll();
        Map<Long, List<Item>> itemsForRequest = itemRepository.findByRequestIdIsNotNull().stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapping.convertToGetDto(itemRequest,
                        itemsForRequest.getOrDefault(itemRequest.getId(), new ArrayList<>())))
                .sorted(Comparator.comparing(ItemRequestDto::getCreateDate))
                .collect(Collectors.toList());
    }

    private User findUserForRequest(long authorId) {
        return userRepository.findById(authorId).orElseThrow(() -> new NotFoundException(
                String.format("User with id = %d not found", authorId)));
    }
}
