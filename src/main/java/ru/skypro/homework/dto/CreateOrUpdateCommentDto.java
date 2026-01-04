package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Create or update comment")
public class CreateOrUpdateCommentDto {
    @Schema(description = "Text")
    private String text;
}
