package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для передачи данных о комментарии.
 * Содержит информацию о комментарии и его авторе.
 */
@Data
@Schema(description = "Comment")
public class CommentDto {
    private Integer pk;
    private Integer author;
    private String authorFirstName;
    private String authorImage;
    private Long createdAt;
    private String text;
}
