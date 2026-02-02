package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.service.AdService;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @Operation(summary = "получить всю ads")
    @GetMapping
    public AdsDto getAllAds() {
        return adService.getAllAds();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AdDto addAd(
            @RequestPart("properties") CreateOrUpdateAdDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        log.info("Создаём объявление: {}", dto);
        log.info("Файл изображения: {}", image != null ? image.getOriginalFilename() : "нет");

        return adService.createAd(dto, image);
    }

    @Operation(summary = "Get ad")
    @GetMapping("/{id}")
    public ExtendedAdDto getAd(@PathVariable Integer id) {
        return adService.getAdById(id);
    }

    @Operation(summary = "Update ad")
    @PatchMapping("/{id}")
    public AdDto updateAd(
            @PathVariable Integer id,
            @RequestBody CreateOrUpdateAdDto dto
    ) {
        return adService.updateAd(id, dto);
    }

    @Operation(summary = "Delete ad")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAd(@PathVariable Integer id) {
        adService.deleteAd(id);
    }

    @Operation(summary = "Get my ads")
    @GetMapping("/me")
    public AdsDto getMyAds() {
        return adService.getAdsByUser();
    }

    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Обновить изображение объявления")
    public ResponseEntity<Void> updateImage(
            @PathVariable Integer id,
            @RequestPart("image") MultipartFile image) throws IOException {
        adService.updateAdImage(id, image);
        return ResponseEntity.ok().build();
    }
}
