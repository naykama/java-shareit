package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static User convertToEntity(UserDto userDto) {
        return new User(userDto.getId(), userDto.getEmail(), userDto.getName());
    }
}
