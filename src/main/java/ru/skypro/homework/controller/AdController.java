package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;

@RestController
@RequestMapping("/ads")
public class AdController {

    @Operation(summary = "получить всю ads")
    @GetMapping
    public AdsDto getAllAds() {
        return new AdsDto();
    }

    @Operation(summary = "Add ad")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AdDto addAd(
            @RequestPart("properties") CreateOrUpdateAdDto dto,
            @RequestPart("image") MultipartFile image
    ) {
        return new AdDto();
    }

    @Operation(summary = "Get ad")
    @GetMapping("/{id}")
    public ExtendedAdDto getAd(@PathVariable Integer id) {
        return new ExtendedAdDto();
    }

    @Operation(summary = "Update ad")
    @PatchMapping("/{id}")
    public AdDto updateAd(
            @PathVariable Integer id,
            @RequestBody CreateOrUpdateAdDto dto
    ) {
        return new AdDto();
    }

    @Operation(summary = "Delete ad")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAd(@PathVariable Integer id) {
    }

    @Operation(summary = "Get my ads")
    @GetMapping("/me")
    public AdsDto getMyAds() {
        return new AdsDto();
    }

    @Operation(summary = "Update ad image")
    @PatchMapping("/{id}/image")
    public byte[] updateImage(@PathVariable Integer id, @RequestParam MultipartFile image) {
        return new byte[0];
    }

}
