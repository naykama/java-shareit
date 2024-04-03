package ru.practicum.shareit.user;

import java.util.List;
import java.util.Map;

public interface UserService {
    User createUser(User user);

    List<User> findAllUsers();

    User findUserById(long id);

    User updateUser(long id, Map<String, String> userUpdatedParams);

    void deleteUser(long id);
}
