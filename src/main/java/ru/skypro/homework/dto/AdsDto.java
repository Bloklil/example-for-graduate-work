package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO для передачи списка объявлений.
 * Содержит общее количество объявлений и список DTO объявлений.
 */
@Data
@Schema(description = "Ads list")
public class AdsDto {
    @Schema(description = "Total count")
    private Integer count;

    @Schema(description = "Ads")
    private List<AdDto> results = List.of();
}
