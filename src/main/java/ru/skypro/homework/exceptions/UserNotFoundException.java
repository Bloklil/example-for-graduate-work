package ru.skypro.homework.exceptions;

/**
 * Исключение, выбрасываемое когда пользователь не найден в базе данных.
 */
public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
