package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Login request")
public class Login {

    @Schema(description = "Username", minLength = 4, maxLength = 32)
    private String username;

    @Schema(description = "Password", minLength = 8, maxLength = 16)
    private String password;
}
