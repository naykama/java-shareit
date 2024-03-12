package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.CommentMapper.convertToEntity;
import static ru.practicum.shareit.item.dto.ItemMapper.convertToGetDto;
import static ru.practicum.shareit.item.dto.CommentMapper.convertToGetDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item createItem(Item item) {
        findUserForItem(item.getOwnerId());
        Item createdItem = itemRepository.save(item);
        log.info("Item with id = {} created", createdItem.getId());
        return createdItem;
    }

    @Override
    public List<GetItemDto> getAllItemsForOwner(long ownerId) {
        List<Item> items = itemRepository.getByOwnerId(ownerId);
        log.info("Items for owner with id = {} found, count of items = {}", ownerId, items.size());
        return items.stream()
                .map(item -> getItemDtoWithComments(getItemDtoWithBookings(item, ownerId)))
                .sorted(Comparator.comparing(GetItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public GetItemDto getItemById(long id, long ownerId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException(
                                                                    String.format("Item with id = %d not found", id)));
        log.info("Item with id = {} found", id);
        return getItemDtoWithComments(getItemDtoWithBookings(item, ownerId));
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
    public List<Item> searchItems(String text) {
        log.info("Items for search with text = {} found", text);
        return text.isEmpty() ? new ArrayList<>() : itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableToRentIsTrue(text, text);
    }

    @Override
    public GetCommentDto createComment(String text, LocalDateTime createDate, long userId, long itemId) {
        if (StringUtils.isEmpty(text)) {
            log.error("Comment needs to have text");
            throw new IllegalArgumentException("Comment doesn't have text");
        }
        log.debug("createComment: перед findForCheckComment");
        if (bookingRepository.findForCheckComment(itemId, userId, createDate).size() == 0) {
            log.error("Not found booking with userId = {} and itemId = {}", userId, itemId);
            throw new IllegalArgumentException("Not found booking with such user and item");
        }
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("User with id = %d not found", userId)));
        log.debug("createComment: комментарию найден автор");
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                String.format("Item with id = %d not found", itemId)));
        return convertToGetDto(commentRepository.save(convertToEntity(text, createDate, author, item)));
    }

    private List<GetCommentDto> findCommentsForItem(long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::convertToGetDto)
                .collect(Collectors.toList());
    }

    private User findUserForItem(long ownerId) {
        return userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                String.format("User with id = %d not found", ownerId)));
    }

    private List<Booking> getBookingsForItem(long itemId) {
        return bookingRepository.findByItemId(itemId).stream()
            .sorted(Comparator.comparing(Booking::getStartDate))
            .collect(Collectors.toList());
    }

    private GetItemDto getItemDtoWithBookings(Item item, long ownerId) {
        if (ownerId != item.getOwnerId()) {
            return convertToGetDto(item, null, null);
        }
        List<BookingDtoWithoutItem> bookings = getBookingsForItem(item.getId()).stream()
                .filter(booking -> booking.getStatus() != Booking.StatusType.REJECTED)
                .map(BookingMapper::convertToDtoWithoutItem)
                .collect(Collectors.toList());
        BookingDtoWithoutItem lastBooking = bookings.stream()
                .filter(booking -> booking.getStartDate().isBefore(LocalDateTime.now()))
                .reduce((first, second) -> second).orElse(null);
        BookingDtoWithoutItem nextBooking = bookings.stream()
                .filter(booking -> booking.getStartDate().isAfter(LocalDateTime.now()))
                .reduce((first, second) -> first).orElse(null);
        return convertToGetDto(item, lastBooking, nextBooking);
    }

    private GetItemDto getItemDtoWithComments(GetItemDto item) {
        item.setComments(findCommentsForItem(item.getId()));
        return item;
    }
}
