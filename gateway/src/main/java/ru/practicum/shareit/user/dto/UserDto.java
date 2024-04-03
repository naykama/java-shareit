package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@Data
public class UserDto {
    private final long id;
    @NotEmpty(message = "email cannot be empty")
    @Email(message = "email is not correct")
    private final String email;
    private final String name;
}

