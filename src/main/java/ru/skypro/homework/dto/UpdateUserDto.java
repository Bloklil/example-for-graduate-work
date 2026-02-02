package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для обновления информации о пользователе.
 * Содержит поля, которые могут быть обновлены в профиле пользователя.
 */
@Data
@Schema(description = "Update user")
public class UpdateUserDto {

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Phone number")
    private String phone;

}
