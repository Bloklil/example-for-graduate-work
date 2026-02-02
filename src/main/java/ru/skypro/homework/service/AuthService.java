package ru.skypro.homework.service;

import ru.skypro.homework.dto.Register;

/**
 * Сервис аутентификации и регистрации пользователей.
 * Предоставляет методы для входа в систему и регистрации новых пользователей.
 */
public interface AuthService {

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param userName имя пользователя (email) для входа
     * @param password пароль пользователя
     * @return {@code true} если аутентификация успешна, {@code false} в противном случае
     */
    boolean login(String userName, String password);

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param register DTO с данными для регистрации пользователя
     * @return {@code true} если регистрация успешна, {@code false} если пользователь уже существует
     *         или произошла ошибка при регистрации
     */
    boolean register(Register register);
}
