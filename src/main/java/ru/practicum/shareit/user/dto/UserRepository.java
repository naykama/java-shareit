package ru.practicum.shareit.user.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepository implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User createUser(User user) {
        if (getAllEmails().contains(user.getEmail())) {
            log.error("User with email = {} is already exist", user.getEmail());
            throw new AlreadyExistException(String.format("User with email = %s is already exist", user.getEmail()));
        }
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            log.error("User with id = {} not found", id);
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
        return users.get(id);
    }

    @Override
    public User updateUser(long id, Map<String, String> updatedParams) {
        if (!users.containsKey(id)) {
            log.error("User with id = {} not found", id);
            throw new NotFoundException(String.format("User with id = %d not found", id));
        }
        User user = users.get(id);
        for (String key : updatedParams.keySet()) {
            switch (key) {
                case "email":
                    String email = updatedParams.get("email");
                    if (getAllEmails().contains(email) && !user.getEmail().equals(email)) {
                        log.error("Other user with email = {} is already exist", user.getEmail());
                        throw new AlreadyExistException(String.format("Other user with email = %s is already exist", user.getEmail()));
                    }
                    user.setEmail(email);
                    break;
                case "name":
                    user.setName(updatedParams.get("name"));
                    break;
            }
        }
        return user;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    private Set<String> getAllEmails() {
        Set<String> emails = new HashSet<>();
        for (User user : users.values()) {
            emails.add(user.getEmail());
        }
        return emails;
    }
}
