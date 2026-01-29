package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.AdNotFoundException;
import ru.skypro.homework.mappers.AdMapper;
import ru.skypro.homework.mappers.CollectionMapper;
import ru.skypro.homework.repositories.AdRepository;
import ru.skypro.homework.repositories.UserRepository;
import ru.skypro.homework.service.FileService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdMapper adMapper;

    @Mock
    private FileService fileService;

    @Mock
    private CollectionMapper collectionMapper;
    @InjectMocks
    private AdServiceImpl adService;

    private UserEntity testUser;
    private UserEntity testAdmin;
    private AdEntity testAd;
    private CreateOrUpdateAdDto testCreateDto;

    private void setupCurrentUser(String email) {
        // Имитируем пользователя
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @BeforeEach
    void setUp() {
        // Создаем обычного пользователя
        testUser = new UserEntity();
        testUser.setId(1);
        testUser.setEmail("user@example.com");
        testUser.setRole(Role.USER);

        // Создаем администратора
        testAdmin = new UserEntity();
        testAdmin.setId(2);
        testAdmin.setEmail("admin@example.com");
        testAdmin.setRole(Role.ADMIN);

        // Создаем тестовое объявление
        testAd = new AdEntity();
        testAd.setId(1);
        testAd.setTitle("Продам машину");
        testAd.setAuthor(testUser);
        testAd.setImage("old-image.jpg");

        // Создаем тестовый DTO (добавлено)
        testCreateDto = new CreateOrUpdateAdDto();
        testCreateDto.setTitle("Новый заголовок");
        testCreateDto.setDescription("Новое описание");
        testCreateDto.setPrice(1000);
    }

    @AfterEach
    void tearDown() {
        // Очищаем SecurityContext после каждого теста
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllAds_shouldReturnAllAds() {
        List<AdEntity> ads = List.of(testAd);
        AdsDto expectedDto = new AdsDto();
        expectedDto.setCount(1);

        when(adRepository.findAll()).thenReturn(ads);
        when(collectionMapper.adsToDto(ads)).thenReturn(expectedDto);

        AdsDto result = adService.getAllAds();

        // Проверяем, что результат не null
        assertNotNull(result);
        // Проверяем количество объявлений
        assertEquals(1, result.getCount());
        // Проверяем, что метод вызвался 1 раз
        verify(adRepository, times(1)).findAll();
    }

    @Test
    void getAdById_whenAdNotFound_shouldThrowException() {
        Integer nonExistingId = 999;

        when(adRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Проверяем, что вызов метода выбросит исключение
        assertThrows(AdNotFoundException.class, () -> {
            adService.getAdById(nonExistingId);
        });

        // Также можно проверить, что метод был вызван
        verify(adRepository, times(1)).findById(nonExistingId);
    }

    @Test
    void createAd_withValidData_shouldCreateAd() throws IOException {
        setupCurrentUser("user@example.com");

        // Создаем тестовую картинку
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "laptop.jpg",
                "image/jpeg",
                "content".getBytes());

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));

        AdEntity newAd = new AdEntity();
        newAd.setId(1);
        newAd.setTitle("Новая машина");

        when(adMapper.toEntity(testCreateDto, testUser))
                .thenReturn(newAd);

        when(fileService.saveImage(image))
                .thenReturn("saved-image.jpg");

        when(adRepository.save(newAd))
                .thenReturn(newAd);

        AdDto expectedDto = new AdDto();
        expectedDto.setPk(1);
        expectedDto.setTitle("Новая машина");

        when(adMapper.toDto(newAd))
                .thenReturn(expectedDto);

        AdDto result = adService.createAd(testCreateDto, image);

        //Проверяем, что результат не null
        assertNotNull(result, "Результат не должен быть null");

        //Проверяем конкретные поля
        assertEquals(1, result.getPk(), "ID должен быть 1");
        assertEquals("Новая машина", result.getTitle(), "Название должно совпадать");

        //Проверяем, что имя файла установлено правильно
        assertEquals("saved-image.jpg", newAd.getImage(), "Имя файла должно быть saved-image.jpg");

        verify(fileService, times(1)).saveImage(image);
        verify(adRepository, times(1)).save(newAd);
        verify(userRepository, times(1)).findByEmail("user@example.com");
    }

    @Test
    void updateAd_whenUserIsAuthor_shouldUpdateAd() {
        // Пользователь авторизован
        setupCurrentUser("user@example.com");

        when(adRepository.findById(1)).thenReturn(Optional.of(testAd));

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));

        when(adRepository.save(testAd)).thenReturn(testAd);

        AdDto result = adService.updateAd(1, testCreateDto);

        // Проверяем, что метод обновления сущности вызвался
        verify(adMapper, times(1)).updateEntityFromDto(testAd, testCreateDto);
        // Проверяем, что объявление сохранилось
        verify(adRepository, times(1)).save(testAd);
    }

    @Test
    void updateAd_whenUserIsNotAuthorNorAdmin_shouldThrowException() {
        // Создаем третьего пользователя (не автора и не админа)
        UserEntity otherUser = new UserEntity();
        otherUser.setId(3);
        otherUser.setEmail("other@example.com");
        otherUser.setRole(Role.USER);

        setupCurrentUser("other@example.com");

        when(adRepository.findById(1)).thenReturn(Optional.of(testAd));
        when(userRepository.findByEmail("other@example.com"))
                .thenReturn(Optional.of(otherUser));

        // Ожидаем, что будет выброшено исключение AccessDeniedException
        assertThrows(AccessDeniedException.class, () -> {
            adService.updateAd(1, testCreateDto);
        });
    }

    @Test
    void deleteAd_whenUserIsAuthor_shouldDeleteAd() throws IOException {
        setupCurrentUser("user@example.com");

        when(adRepository.findById(1)).thenReturn(Optional.of(testAd));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));

        adService.deleteAd(1);

        // Проверяем, что картинка удалилась из файловой системы
        verify(fileService, times(1)).deleteImage("old-image.jpg");
        // Проверяем, что объявление удалилось из БД
        verify(adRepository, times(1)).delete(testAd);
    }

    @Test
    void updateAdImage_whenUserIsAuthor_shouldUpdateImage() throws IOException {
        setupCurrentUser("user@example.com");

        MockMultipartFile newImage = new MockMultipartFile(
                "image", "new.jpg", "image/jpeg", "new content".getBytes()
        );

        when(adRepository.findById(1)).thenReturn(Optional.of(testAd));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));
        when(fileService.saveImage(newImage)).thenReturn("new-image.jpg");

        adService.updateAdImage(1, newImage);

        // Старая картинка должна удалиться
        verify(fileService, times(1)).deleteImage("old-image.jpg");
        // Новая картинка должна сохраниться
        verify(fileService, times(1)).saveImage(newImage);
        // Имя файла должно обновиться
        assertEquals("new-image.jpg", testAd.getImage());
    }
}