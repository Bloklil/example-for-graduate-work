package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для создания или обновления комментария.
 * Используется как входные данные для операций создания и обновления комментариев.
 */
@Data
@Schema(description = "Create or update comment")
public class CreateOrUpdateCommentDto {
    @Schema(description = "Text")
    private String text;
}
