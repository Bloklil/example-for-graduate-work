package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * DTO для передачи данных о пользователе.
 * Содержит полную информацию о пользователе для отображения в API.
 */
@Data
@Schema(description = "User")
public class UserDto {

    @Schema(description = "User ID")
    private Integer id;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Phone")
    private String phone;

    @Schema(description = "Role")
    private Role role;

    @Schema(description = "Avatar image URL")
    private String image;

}
