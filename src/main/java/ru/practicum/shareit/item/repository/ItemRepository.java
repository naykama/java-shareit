package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getByOwnerId(long ownerId);

    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableToRentIsTrue(String text1,
                                                                                                        String text2);

    List<Item> findByRequestIdIsNotNull();

    List<Item> findByRequestId(long requestId);
}
