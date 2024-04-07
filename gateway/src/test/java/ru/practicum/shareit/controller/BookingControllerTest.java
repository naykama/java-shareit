package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private static final LocalDateTime START_DAY = LocalDateTime.now().plusDays(1);
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingClient bookingClient;
    @Autowired
    MockMvc mvc;

    @Test
    public void createBookingNotValidTest() throws Exception {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, START_DAY, START_DAY.plusDays(5));
        when(bookingClient.bookItem(anyLong(), any(BookItemRequestDto.class))).thenThrow(IllegalArgumentException.class);

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createBookingErrorTest() throws Exception {
        BookItemRequestDto bookingDto = new BookItemRequestDto(1L, START_DAY, START_DAY.plusDays(5));
        when(bookingClient.bookItem(anyLong(), any(BookItemRequestDto.class))).thenThrow(RuntimeException.class);

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
