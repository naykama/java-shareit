package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.*;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@Data
public class User {
    private long id;
    @NotEmpty(message = "email cannot be empty")
    @Email(message = "email is not correct")
    private String email;
    private String name;
}
