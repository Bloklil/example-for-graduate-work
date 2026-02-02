package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для запроса на регистрацию нового пользователя.
 * Содержит все необходимые данные для создания нового аккаунта.
 */
@Data
@Schema(description = "Register request")
public class Register {

    @Schema(description = "Username")
    private String username;

    @Schema(description = "Password")
    private String password;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Phone number")
    private String phone;

    @Schema(description = "User role")
    private Role role;
}
