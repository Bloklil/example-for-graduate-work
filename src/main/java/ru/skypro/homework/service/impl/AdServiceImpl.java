package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.AdNotFoundException;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.mappers.AdMapper;
import ru.skypro.homework.mappers.CollectionMapper;
import ru.skypro.homework.repositories.AdRepository;
import ru.skypro.homework.repositories.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.FileService;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Реализация сервиса для управления объявлениями.
 * Обрабатывает бизнес-логику работы с объявлениями, включая проверку прав доступа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final CollectionMapper collectionMapper;
    private final FileService fileService;

    /**
     * Получает текущего аутентифицированного пользователя из контекста безопасности.
     *
     * @return сущность текущего пользователя
     * @throws UserNotFoundException если пользователь не найден в базе данных
     */
    private UserEntity getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public AdsDto getAllAds() {
        List<AdEntity> ads = adRepository.findAll();
        log.debug("Found {} ads", ads.size());
        return collectionMapper.adsToDto(ads);
    }

    /**
     * {@inheritDoc}
     * Требует аутентификации пользователя.
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public AdsDto getAdsByUser() {
        UserEntity currentUser = getCurrentUserEntity();
        List<AdEntity> userAds = adRepository.findAllByAuthor(currentUser);
        log.debug("Found {} ads for user: {}", userAds.size(), currentUser.getEmail());
        return collectionMapper.adsToDto(userAds);
    }

    /**
     * {@inheritDoc}
     * Требует роли USER или ADMIN.
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AdDto createAd(CreateOrUpdateAdDto createAdDto, MultipartFile image) throws IOException {

        UserEntity author = getCurrentUserEntity();
        AdEntity adEntity = adMapper.toEntity(createAdDto, author);

        String filename = fileService.saveImage(image);
        adEntity.setImage(filename);

        AdEntity savedAd = adRepository.save(adEntity);
        log.info("Ad created: {} by user: {}", savedAd.getId(), author.getEmail());
        return adMapper.toDto(savedAd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ExtendedAdDto getAdById(Integer id) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + id));
        log.debug("Found ad with id: {}", id);
        return adMapper.toExtendedDto(adEntity);
    }

    /**
     * {@inheritDoc}
     * Требует аутентификации пользователя.
     * Проверяет права доступа: обновлять может только автор объявления или администратор.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public AdDto updateAd(Integer id, CreateOrUpdateAdDto updateAdDto) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + id));

        UserEntity currentUser = getCurrentUserEntity();

        boolean isAuthor = adEntity.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to update this ad");
        }

        adMapper.updateEntityFromDto(adEntity, updateAdDto);
        AdEntity updatedAd = adRepository.save(adEntity);
        log.info("Ad updated: {} by user: {}", id, currentUser.getEmail());
        return adMapper.toDto(updatedAd);
    }

    /**
     * {@inheritDoc}
     * Требует аутентификации пользователя.
     * Проверяет права доступа: удалять может только автор объявления или администратор.
     * Также удаляет связанное изображение, если оно существует.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void deleteAd(Integer id) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + id));

        UserEntity currentUser = getCurrentUserEntity();

        boolean isAuthor = adEntity.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this ad");
        }

        if (adEntity.getImage() != null) {
            try {
                fileService.deleteImage(adEntity.getImage());
            } catch (IOException e) {
                log.warn("Failed to delete image for ad {}: {}", id, e.getMessage());
            }
        }

        adRepository.delete(adEntity);
        log.info("Ad deleted: {} by user: {}", id, currentUser.getEmail());
    }

    /**
     * {@inheritDoc}
     * Требует аутентификации пользователя.
     * Проверяет права доступа: обновлять изображение может только автор объявления или администратор.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updateAdImage(Integer id, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image cannot be null or empty");
        }

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + id));

        UserEntity currentUser = getCurrentUserEntity();

        boolean isAuthor = adEntity.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to update this ad");
        }

        if (adEntity.getImage() != null) {
            fileService.deleteImage(adEntity.getImage());
        }

        String filename = fileService.saveImage(image);
        adEntity.setImage(filename);
        adRepository.save(adEntity);
        log.info("Image updated for ad: {} by user: {}", id, currentUser.getEmail());
    }

    /**
     * {@inheritDoc}
     * Используется для прямого доступа к файлам изображений.
     */
    public byte[] getAdImageById(Integer id) throws IOException {
        AdEntity ad = adRepository.findById(id).orElse(null);
        if (ad == null || ad.getImage() == null) {
            return null;
        }
        Path imagePath = Paths.get("images", ad.getImage()); // папка images на уровне проекта
        if (!Files.exists(imagePath)) {
            return null;
        }
        return Files.readAllBytes(imagePath);
    }


}
