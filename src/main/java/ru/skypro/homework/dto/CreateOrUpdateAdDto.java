package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
