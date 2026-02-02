package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для создания или обновления объявления.
 * Используется как входные данные для операций создания и обновления объявлений.
 */
@Data
@Getter
@Setter
@Schema(description = "Create or update ad")
public class CreateOrUpdateAdDto {

    @Schema(description = "Title")
    private String title;

    @Schema(description = "Price")
    private Integer price;

    @Schema(description = "Description")
    private String description;

}
