package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class CustomPage {
    public static Pageable getPage(Integer from, Integer size, Sort sort) {
        from = from == null ? 0 : from;
        size = size == null ? Integer.MAX_VALUE : size;
        return PageRequest.of(from / size, size, sort);
    }

    public static Pageable getPage(Integer from, Integer size) {
        from = from == null ? 0 : from;
        size = size == null ? Integer.MAX_VALUE : size;
        return PageRequest.of(from / size, size);
    }
}
