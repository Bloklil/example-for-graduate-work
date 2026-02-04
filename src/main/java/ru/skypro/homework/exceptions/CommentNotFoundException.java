package ru.skypro.homework.exceptions;

/**
 * Исключение, выбрасываемое когда комментарий не найден в базе данных.
 */
public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(String message) {
        super(message);
    }
}
