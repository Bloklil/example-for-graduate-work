package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Comments list")
public class CommentsDto {
    private Integer count;
    private List<CommentDto> results = List.of();
}
