package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static ru.practicum.shareit.request.dto.ItemRequestMapping.convertToDto;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequest request) {
        findUserForRequest(request.getAuthorId());
        return convertToDto(requestRepository.save(request));
    }

    private User findUserForRequest(long authorId) {
        return userRepository.findById(authorId).orElseThrow(() -> new NotFoundException(
                String.format("User with id = %d not found", authorId)));
    }
}
