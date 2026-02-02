package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Расширенный DTO для передачи полной информации об объявлении.
 * Содержит дополнительные данные об авторе объявления.
 */
@Data
@Schema(description = "Extended ad")
public class ExtendedAdDto {

    private Integer pk;
    private String title;
    private Integer price;
    private String description;
    private String image;
    private String email;
    private String phone;
    private String authorFirstName;
    private String authorLastName;

}
