package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService service;
    @Autowired
    MockMvc mvc;

    @Test
    public void createUserTest() throws Exception {
        User user = new User(1L, "er@mail.ru", "Name");
        when(service.createUser(any(User.class))).thenReturn(user);
        mvc.perform(post("/users").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findAllUsersTest() throws Exception {
        User user = new User(1L, "er@mail.ru", "Name");
        when(service.findAllUsers()).thenReturn(List.of(user));
        mvc.perform(get("/users").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void findUserByIdTest() throws Exception {
        User user = new User(1L, "er@mail.ru", "Name");
        when(service.findUserById(anyLong())).thenReturn(user);
        mvc.perform(get("/users/1").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserTest() throws Exception {
        User user = new User(1L, "er@mail.ru", "Name");
        when(service.updateUser(anyLong(), anyMap())).thenReturn(user);
        mvc.perform(patch("/users/1").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserTestExp() throws Exception {
        User user = new User(1L, "er@mail.ru", "Name");
        when(service.updateUser(anyLong(), anyMap())).thenThrow(AlreadyExistException.class);
        mvc.perform(patch("/users/1").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void deleteUserTest() throws Exception {
        User user = new User(1L, "er@mail.ru", "Name");
        mvc.perform(delete("/users/1").header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
