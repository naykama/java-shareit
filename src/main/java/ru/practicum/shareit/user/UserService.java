package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserStorage;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        return userStorage.getUserById(id);
    }

    public User updateUser(long id, Map<String, String> userUpdatedParams) {
        return userStorage.updateUser(id, userUpdatedParams);
    }

    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }
}
