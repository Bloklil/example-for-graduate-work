package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;

import java.io.IOException;

public interface AdService {

    AdsDto getAllAds();

    AdsDto getAdsByUser();

    AdDto createAd(CreateOrUpdateAdDto createAdDto, MultipartFile image) throws IOException;

    ExtendedAdDto getAdById(Integer id);

    AdDto updateAd(Integer id, CreateOrUpdateAdDto updateAdDto);

    void deleteAd(Integer id);

    void updateAdImage(Integer id, MultipartFile image) throws IOException;

    byte[] getAdImageById(Integer id) throws IOException;
}
