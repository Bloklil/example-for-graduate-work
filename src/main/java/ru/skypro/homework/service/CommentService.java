package ru.skypro.homework.service;

import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;

/**
 * Сервис для управления комментариями к объявлениям.
 * Предоставляет операции для работы с комментариями.
 */
public interface CommentService {

    /**
     * Получает все комментарии для указанного объявления.
     *
     * @param adId идентификатор объявления
     * @return DTO со списком комментариев к объявлению
     * @throws ru.skypro.homework.exceptions.AdNotFoundException если объявление не найдено
     */
    CommentsDto getComments(Integer adId);

    /**
     * Создает новый комментарий к объявлению.
     *
     * @param adId идентификатор объявления
     * @param commentDto DTO с данными комментария
     * @return DTO созданного комментария
     * @throws ru.skypro.homework.exceptions.AdNotFoundException если объявление не найдено
     * @throws ru.skypro.homework.exceptions.UserNotFoundException если пользователь не найден
     */
    CommentDto createComment(Integer adId, CreateOrUpdateCommentDto commentDto);

    /**
     * Обновляет существующий комментарий.
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param commentDto DTO с новыми данными комментария
     * @return обновленное DTO комментария
     * @throws ru.skypro.homework.exceptions.AdNotFoundException если объявление не найдено
     * @throws ru.skypro.homework.exceptions.CommentNotFoundException если комментарий не найден
     * @throws org.springframework.security.access.AccessDeniedException если пользователь не имеет прав на обновление
     */
    CommentDto updateComment(Integer adId, Integer commentId, CreateOrUpdateCommentDto commentDto);

    /**
     * Удаляет комментарий.
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @throws ru.skypro.homework.exceptions.AdNotFoundException если объявление не найдено
     * @throws ru.skypro.homework.exceptions.CommentNotFoundException если комментарий не найден
     * @throws org.springframework.security.access.AccessDeniedException если пользователь не имеет прав на удаление
     */
    void deleteComment(Integer adId, Integer commentId);
}
