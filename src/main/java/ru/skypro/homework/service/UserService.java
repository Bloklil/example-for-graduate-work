package ru.skypro.homework.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.exceptions.UserNotFoundException;

import java.io.IOException;

/**
 * Сервис для управления профилями пользователей.
 * Предоставляет операции для работы с данными пользователей.
 */
public interface UserService {

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @return DTO текущего пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    UserDto getCurrentUser();

    /**
     * Обновляет информацию о текущем пользователе.
     *
     * @param updateUserDto DTO с новыми данными пользователя
     * @return обновленное DTO пользователя
     * @throws UserNotFoundException если пользователь не найден
     * @throws AccessDeniedException если пользователь пытается изменить не свой профиль
     */
    UserDto updateUser(UpdateUserDto updateUserDto);

    /**
     * Изменяет пароль текущего пользователя.
     *
     * @param newPasswordDto DTO с текущим и новым паролем
     * @throws UserNotFoundException если пользователь не найден
     * @throws IllegalArgumentException если текущий пароль неверен
     * @throws AccessDeniedException если пользователь пытается изменить не свой пароль
     */
    void updatePassword(NewPasswordDto newPasswordDto);

    /**
     * Обновляет аватар текущего пользователя.
     *
     * @param image файл с новым изображением аватара
     * @throws IOException если произошла ошибка при сохранении изображения
     * @throws IllegalArgumentException если изображение null или пустое
     * @throws ru.skypro.homework.exceptions.UserNotFoundException если пользователь не найден
     * @throws org.springframework.security.access.AccessDeniedException если пользователь пытается изменить не свой аватар
     */
    void updateUserImage(MultipartFile image) throws IOException;
}
