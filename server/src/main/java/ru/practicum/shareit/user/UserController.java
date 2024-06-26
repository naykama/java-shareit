package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.UserMapper.convertToDto;
import static ru.practicum.shareit.user.dto.UserMapper.convertToEntity;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = convertToEntity(userDto);
        return convertToDto(userService.createUser(user));
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        return userService.findAllUsers().stream()
                .map(UserMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable long id) {
        return convertToDto(userService.findUserById(id));
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable long id, @RequestBody Map<String, String> formParams) {
        return convertToDto(userService.updateUser(id, formParams));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
