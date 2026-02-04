package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление ролей пользователей в системе.
 * Определяет возможные роли: USER (обычный пользователь) и ADMIN (администратор).
 */
@Schema(description = "User role")
public enum Role {
    USER, ADMIN
}
