package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Change password request")
public class NewPasswordDto {

    @Schema(description = "Current password")
    private String currentPassword;

    @Schema(description = "New password")
    private String newPassword;
}
