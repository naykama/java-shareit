package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    private static final LocalDateTime START_DAY = LocalDateTime.now().plusDays(1);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:mm:ss");
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    MockMvc mvc;

    @Test
    public void createBookingTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, START_DAY, START_DAY.plusDays(5));
        GetBookingDto getBookingDto = createGetBooking();
        when(bookingService.createBooking(bookingDto, 1L)).thenReturn(getBookingDto);

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", getBookingDto.getBooker().getId())
                .content(mapper.writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(getBookingDto.getBookingId()), Long.class))
            .andExpect(jsonPath("$.status", is(getBookingDto.getStatus().toString())))
            .andExpect(jsonPath("$.booker.id", is(getBookingDto.getBooker().getId()), Long.class))
            .andExpect(jsonPath("$.item.id", is(getBookingDto.getItem().getId()), Long.class));
    }

    @Test
    public void createBookingNotValidTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, START_DAY, START_DAY.plusDays(5));
        GetBookingDto getBookingDto = createGetBooking();
        when(bookingService.createBooking(any(BookingDto.class), anyLong())).thenThrow(IllegalArgumentException.class);

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", getBookingDto.getBooker().getId())
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createBookingErrorTest() throws Exception {
        BookingDto bookingDto = new BookingDto(1L, START_DAY, START_DAY.plusDays(5));
        when(bookingService.createBooking(any(BookingDto.class), anyLong())).thenThrow(RuntimeException.class);

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void responseBookingTest() throws Exception {
        GetBookingDto getBookingDto = createGetBooking();
        when(bookingService.responseBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(getBookingDto);
        mvc.perform(patch("/bookings/1").header("X-Sharer-User-Id", 1)
                        .param("approved", Boolean.toString(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getBookingDto.getBookingId()), Long.class));
    }

    @Test
    public void findBookingByIdTest() throws Exception {
        GetBookingDto getBookingDto = createGetBooking();
        when(bookingService.findBookingById(anyLong(), anyLong())).thenReturn(getBookingDto);
        mvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getBookingDto.getBookingId()), Long.class));
    }

    @Test
    public void findBookingForCurrentUserTest() throws Exception {
        GetBookingDto getBookingDto = createGetBooking();
        when(bookingService.findBookingForCurrentUser(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Arrays.asList(getBookingDto));
        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
                        .param("state", "All").param("from", "0").param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findBookingForOwnerTest() throws Exception {
        GetBookingDto getBookingDto = createGetBooking();
        when(bookingService.findBookingForOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(Arrays.asList(getBookingDto));
        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1)
                        .param("state", "All").param("from", "0").param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private GetBookingDto createGetBooking() {
        return new GetBookingDto(1L, START_DAY, START_DAY.plusDays(5), Booking.StatusType.WAITING,
                new UserDto(1L, "booker@mail.ru", "booker"),
                new ItemDto(1L, "item", "descr", true));
    }
}
