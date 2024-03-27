package ru.practicum.shareit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserServiceIntegrationTest {
    @Autowired
    private UserService service;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void deleteUserTest() {
        userRepository.save(createUser());
        assertEquals(1, service.findAllUsers().size());
        userRepository.deleteById(createUser().getId());
        assertEquals(0, service.findAllUsers().size());
    }

    private User createUser() {
        return new User(1L, "owner1@mail.ru", "owner1");
    }
}
