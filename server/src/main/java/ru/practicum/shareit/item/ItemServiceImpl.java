package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.CommentMapper.convertToEntity;
import static ru.practicum.shareit.item.dto.CommentMapper.convertToGetDto;
import static ru.practicum.shareit.item.dto.ItemMapper.convertToDto;
import static ru.practicum.shareit.item.dto.ItemMapper.convertToGetDto;
import static ru.practicum.shareit.utils.CustomPage.getPage;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, long ownerId) {
        findUserForItem(ownerId);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = findRequestForItem(itemDto.getRequestId());
        }
        Item createdItem = itemRepository.save(ItemMapper.convertToEntity(itemDto, ownerId, request));
        log.info("Item with id = {} created", createdItem.getId());
        return convertToDto(createdItem);
    }

    @Override
    public List<GetItemDto> findAllItemsForOwner(long ownerId, Integer from, Integer size) {
        Map<Long, Item> items =  itemRepository.getByOwnerId(ownerId, getPage(from, size)).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<Booking>> bookings = bookingRepository.findByItemIdInAndStatusNot(new ArrayList<>(items.keySet()),
                                                                    Booking.StatusType.REJECTED)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Comment>> comments = commentRepository.findByItemIdIn(items.keySet()).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        log.info("Items found, count of items = {}", items.size());
        return items.values().stream()
                .map(item -> convertToGetDto(item,
                        getLastBooking(bookings.getOrDefault(item.getId(), Collections.emptyList())),
                        getNextBooking(bookings.getOrDefault(item.getId(), Collections.emptyList())),
                        getCommentDtos(comments.getOrDefault(item.getId(), Collections.emptyList()))))
                .collect(Collectors.toList());
    }

    @Override
    public GetItemDto findItemById(long id, long ownerId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException(
                                                                    String.format("Item with id = %d not found", id)));
        log.info("Item with id = {} found", id);
        List<Comment> comments = commentRepository.findByItemIdIn(new HashSet<>(Collections.singletonList(item.getId())));
        if (ownerId != item.getOwnerId()) {
            return convertToGetDto(item, null, null, comments.stream()
                    .map(CommentMapper::convertToGetDto).collect(Collectors.toList()));
        }
        List<Booking> bookings = bookingRepository.findByItemIdInAndStatusNot(
                Collections.singletonList(item.getId()), Booking.StatusType.REJECTED);
        return convertToGetDto(item, getLastBooking(bookings), getNextBooking(bookings), getCommentDtos(comments));
    }

    @Override
    public Item updateItem(long id, Map<String, String> updatedParams, long ownerId) {
        findUserForItem(ownerId);
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException(
                                                                    String.format("Item with id = %d not found", id)));
        if (ownerId != item.getOwnerId()) {
            log.error("User with id = {} cannot update item with id = {}. He is not owner", ownerId, id);
            throw new NotFoundException(
                    String.format("User with id = %d cannot update item with id = %d. He is not owner", ownerId, id)
            );
        }
        for (String key : updatedParams.keySet()) {
            switch (key) {
                case "name":
                    item.setName(updatedParams.get(key));
                    break;
                case "description":
                    item.setDescription(updatedParams.get(key));
                    break;
                case "available":
                    item.setAvailableToRent(Boolean.parseBoolean(updatedParams.get(key)));
                    break;
            }
        }
        log.info("Item with id = {} updated", item.getId());
        return itemRepository.save(item);
    }

    @Override
    public List<Item> searchItems(String text, Integer from, Integer size) {
        log.info("Items for search with text = {} found", text);
        return text.isEmpty() ? new ArrayList<>() : itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableToRentIsTrue(text, text,
                        getPage(from, size));
    }

    @Override
    public GetCommentDto createComment(String text, LocalDateTime createDate, long userId, long itemId) {
        if (StringUtils.isEmpty(text)) {
            log.error("Comment needs to have text");
            throw new IllegalArgumentException("Comment doesn't have text");
        }
        if (bookingRepository.findForCheckComment(itemId, userId, createDate).size() == 0) {
            log.error("Not found booking with userId = {} and itemId = {}", userId, itemId);
            throw new IllegalArgumentException("Not found booking with such user and item");
        }
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with id = %d not found", userId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Item with id = %d not found", itemId)));
        log.info("Comment from user id = {} to item id = {} was created", userId, itemId);
        return convertToGetDto(commentRepository.save(convertToEntity(text, createDate, author, item)));
    }

    private User findUserForItem(long ownerId) {
        return userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("User with id = %d not found", ownerId)));
    }


    private BookingDtoWithoutItem getLastBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStartDate().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getStartDate))
                .map(BookingMapper::convertToDtoWithoutItem)
                .reduce((first, second) -> second).orElse(null);
    }

    private BookingDtoWithoutItem getNextBooking(List<Booking> bookings) {
        return bookings.stream()
                .filter(booking -> booking.getStartDate().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getStartDate))
                .map(BookingMapper::convertToDtoWithoutItem)
                .reduce((first, second) -> first).orElse(null);
    }

    private List<GetCommentDto> getCommentDtos(List<Comment> comments) {
        return comments
                .stream()
                .map(CommentMapper::convertToGetDto).collect(Collectors.toList());
    }

    private ItemRequest findRequestForItem(long reqestId) {
        return requestRepository.findById(reqestId).orElseThrow(() -> new NotFoundException(
                String.format("Request with id = %d not found", reqestId)));
    }
}
