package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для передачи данных об объявлении.
 * Используется для отображения базовой информации об объявлении в API ответах.
 */
@Data
@Schema(description = "Ad")
public class AdDto {

    @Schema(description = "Ad ID")
    private Integer pk;

    @Schema(description = "Author ID")
    private Integer author;

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Price")
    private Integer price;

    @Schema(description = "Image URL")
    private String image;

}
