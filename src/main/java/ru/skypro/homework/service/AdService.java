package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;

import java.io.IOException;

/**
 * Сервис для управления объявлениями (ads).
 * Предоставляет CRUD операции для работы с объявлениями.
 */
public interface AdService {

    /**
     * Получает список всех объявлений в системе.
     *
     * @return DTO с общим количеством и списком всех объявлений
     */
    AdsDto getAllAds();

    /**
     * Получает объявления текущего аутентифицированного пользователя.
     *
     * @return DTO с объявлениями текущего пользователя
     * @throws ru.skypro.homework.exceptions.UserNotFoundException если пользователь не найден
     */
    AdsDto getAdsByUser();

    /**
     * Создает новое объявление.
     *
     * @param createAdDto DTO с данными для создания объявления
     * @param image файл изображения для объявления (может быть null)
     * @return DTO созданного объявления
     * @throws IOException если произошла ошибка при сохранении изображения
     * @throws ru.skypro.homework.exceptions.UserNotFoundException если пользователь не найден
     */
    AdDto createAd(CreateOrUpdateAdDto createAdDto, MultipartFile image) throws IOException;

    /**
     * Получает расширенную информацию об объявлении по его ID.
     *
     * @param id идентификатор объявления
     * @return расширенное DTO объявления с полной информацией
     * @throws ru.skypro.homework.exceptions.AdNotFoundException если объявление не найдено
     */
    ExtendedAdDto getAdById(Integer id);

    /**
     * Обновляет существующее объявление.
     *
     * @param id идентификатор объявления для обновления
     * @param updateAdDto DTO с новыми данными объявления
     * @return обновленное DTO объявления
     * @throws ru.skypro.homework.exceptions.AdNotFoundException если объявление не найдено
     * @throws org.springframework.security.access.AccessDeniedException если пользователь не имеет прав на обновление
     */
    AdDto updateAd(Integer id, CreateOrUpdateAdDto updateAdDto);

    /**
     * Удаляет объявление по его ID.
     *
     * @param id идентификатор объявления для удаления
     * @throws ru.skypro.homework.exceptions.AdNotFoundException если объявление не найдено
     * @throws org.springframework.security.access.AccessDeniedException если пользователь не имеет прав на удаление
     * @throws IOException если произошла ошибка при удалении изображения
     */
    void deleteAd(Integer id);

    /**
     * Обновляет изображение для объявления.
     *
     * @param id идентификатор объявления
     * @param image новый файл изображения
     * @throws IOException если произошла ошибка при работе с файлом
     * @throws ru.skypro.homework.exceptions.AdNotFoundException если объявление не найдено
     * @throws IllegalArgumentException если изображение null или пустое
     * @throws org.springframework.security.access.AccessDeniedException если пользователь не имеет прав на обновление
     */
    void updateAdImage(Integer id, MultipartFile image) throws IOException;

    /**
     * Получает байтовое представление изображения объявления.
     *
     * @param id идентификатор объявления
     * @return массив байтов изображения или {@code null} если изображение не найдено
     * @throws IOException если произошла ошибка при чтении файла
     */
    byte[] getAdImageById(Integer id) throws IOException;
}
