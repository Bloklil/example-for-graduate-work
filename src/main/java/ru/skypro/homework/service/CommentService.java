package ru.skypro.homework.service;

import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;

public interface CommentService {
    CommentsDto getComments(Integer adId);
    CommentDto createComment(Integer adId, CreateOrUpdateCommentDto commentDto);
    CommentDto updateComment(Integer adId, Integer commentId, CreateOrUpdateCommentDto commentDto);
    void deleteComment(Integer adId, Integer commentId);
}
