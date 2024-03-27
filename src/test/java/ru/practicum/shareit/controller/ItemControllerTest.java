package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.GetCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.dto.ItemMapper.convertToEntity;
import static ru.practicum.shareit.item.dto.ItemMapper.convertToGetDto;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    private static final Integer FROM = 0;
    private static final Integer SIZE = 2;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    MockMvc mvc;

    @Test
    public void createItemTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "item1", "descr", true);
        long ownerId = 1L;
        Item item = convertToEntity(itemDto, ownerId, null);
        when(itemService.createItem(itemDto, 1L)).thenReturn(convertToGetDto(item, null,
                null, new ArrayList<>()));
        mvc.perform(post("/items").header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ownerId), Long.class));
    }

    @Test
    public void findAllItemsForOwnerTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "item1", "descr", true);
        long ownerId = 1L;
        Item item = convertToEntity(itemDto, ownerId, null);
        when(itemService.findAllItemsForOwner(1L, FROM, SIZE)).thenReturn(List.of(convertToGetDto(item,
                null, null, new ArrayList<>())));
        mvc.perform(get("/items").header("X-Sharer-User-Id", ownerId)
                        .param("from", Integer.toString(FROM))
                        .param("size", Integer.toString(SIZE))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findItemByIdTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "item1", "descr", true);
        long ownerId = 1L;
        Item item = convertToEntity(itemDto, ownerId, null);
        when(itemService.findItemById(itemDto.getId(), ownerId)).thenReturn(convertToGetDto(item,
                null, null, new ArrayList<>()));
        mvc.perform(get("/items/1").header("X-Sharer-User-Id", ownerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateItemTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "item1", "descr", true);
        long ownerId = 1L;
        Item item = convertToEntity(itemDto, ownerId, null);
        when(itemService.updateItem(itemDto.getId(), new HashMap<>(), ownerId)).thenReturn(item);
        mvc.perform(patch("/items/1").header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(new HashMap<>()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createCommentTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "item1", "descr", true);
        long ownerId = 1L;
        Item item = convertToEntity(itemDto, ownerId, null);
        Map<String, String> map = new HashMap<>();
        map.put("text", "text");
        when(itemService.createComment(anyString(), any(LocalDateTime.class), anyLong(), anyLong()))
                .thenReturn(new GetCommentDto(1L, "text", "author", LocalDateTime.now()));
        mvc.perform(get("/items/1/comment").header("X-Sharer-User-Id", ownerId)
                        .content(mapper.writeValueAsString(map))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void searchItemsTest() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "item1", "descr", true);
        long ownerId = 1L;
        Item item = convertToEntity(itemDto, ownerId, null);
        when(itemService.searchItems("item", FROM, SIZE)).thenReturn(List.of(item));
        mvc.perform(get("/items/search").header("X-Sharer-User-Id", ownerId)
                        .param("from", Integer.toString(FROM))
                        .param("size", Integer.toString(SIZE))
                        .param("text", "item")
                        .content(mapper.writeValueAsString(new HashMap<>()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
