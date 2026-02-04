package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO для передачи списка комментариев.
 * Содержит общее количество комментариев и список DTO комментариев.
 */
@Data
@Schema(description = "Comments list")
public class CommentsDto {
    private Integer count;
    private List<CommentDto> results = List.of();
}
