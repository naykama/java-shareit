package ru.practicum.shareit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService service;
    @Mock
    private UserRepository userRepository;

    @Test
    public void createUserTest() {
        when(userRepository.save(any(User.class))).thenReturn(createUser());
        assertEquals(1L, service.createUser(createUser()).getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void findAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(createUser()));
        assertEquals(1, service.findAllUsers().size());
    }

    @Test
    public void findUserByIdTest() {
        assertThrows(NotFoundException.class, () -> service.findUserById(1L));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(createUser()));
        assertEquals("owner1@mail.ru", service.findUserById(1L).getEmail());
    }

    @Test
    public void deleteUserByIdTest() {
        service.deleteUser(1L);
        assertEquals(0, service.findAllUsers().size());
    }

    @Test
    public void updateUserTest() {
        User user = createUser();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        Map<String, String> userUpdatedParams = new HashMap<>();
        userUpdatedParams.put("email", "newEmail@mail.ru");
        userUpdatedParams.put("name", "newName");

        assertEquals("newEmail@mail.ru", service.updateUser(user.getId(), userUpdatedParams).getEmail());
    }

    @BeforeEach
    public void setUp() {
        service = new UserServiceImpl(userRepository);
    }

    private User createUser() {
        return new User(1L, "owner1@mail.ru", "owner1");
    }
}
