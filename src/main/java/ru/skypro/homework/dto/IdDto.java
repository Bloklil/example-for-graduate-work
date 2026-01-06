package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "ID response")
public class IdDto {
    @Schema(description = "Entity ID", example = "1")
    private Integer id;
}
