package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        User createdUser = userRepository.save(user);
        log.info("User with id = {} created", createdUser.getId());
        return createdUser;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("List of users found, count of users = {}", users.size());
        return users;
    }

    @Override
    public User findUserById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(
                                        String.format("User with id = %d not found", id)));
        log.info("User with id = {} found", user.getId());
        return user;
    }

    @Override
    public User updateUser(long id, Map<String, String> userUpdatedParams) {
        User user = findUserById(id);
        Set<String> emails = getAllEmails();
        for (String key : userUpdatedParams.keySet()) {
            switch (key) {
                case "email":
                    String email = userUpdatedParams.get("email");
                    if (emails.contains(email) && !user.getEmail().equals(email)) {
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

    @Override
    public void deleteUser(long id) {
        log.info("User with id = {} deleted", id);
        userRepository.deleteById(id);
    }

    private Set<String> getAllEmails() {
        Set<String> emails = new HashSet<>();
        for (User user :  findAllUsers()) {
            emails.add(user.getEmail());
        }
        return emails;
    }
}
