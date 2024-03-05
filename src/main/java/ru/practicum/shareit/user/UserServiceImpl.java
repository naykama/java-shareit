package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRepository;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        User createdUser = userRepository.save(user);
        log.info("User with id = {} created", createdUser.getId());
        return createdUser;
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("List of users found, count of users = {}", users.size());
        return users;
    }

    public User getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(
                                        String.format("User with id = %d not found", id)));
        log.info("User with id = {} found", user.getId());
        return user;
    }

    public User updateUser(long id, Map<String, String> userUpdatedParams) {
        User user = getUserById(id);
        for (String key : userUpdatedParams.keySet()) {
            switch (key) {
                case "email":
                    String email = userUpdatedParams.get("email");
                    if (getAllEmails().contains(email) && !user.getEmail().equals(email)) {
                        log.error("Other user with email = {} is already exist", user.getEmail());
                        throw new AlreadyExistException(String.format("Other user with email = %s is already exist", user.getEmail()));
                    }
                    user.setEmail(email);
                    break;
                case "name":
                    user.setName(userUpdatedParams.get("name"));
                    break;
            }
        }
        log.info("User with id = {} updated", user.getId());
        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        log.info("User with id = {} deleted", id);
        userRepository.deleteById(id);
    }

    private Set<String> getAllEmails() {
        Set<String> emails = new HashSet<>();
        for (User user :  getAllUsers()) {
            emails.add(user.getEmail());
        }
        return emails;
    }
}
