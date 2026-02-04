package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.service.CommentService;

/**
 * Контроллер для работы с комментариями к объявлениям.
 * Обрабатывает операции создания, получения, обновления и удаления комментариев.
 * Комментарии привязаны к конкретным объявлениям через идентификатор ad.
 */
@RestController
@RequestMapping("/ads")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Get comments")
    @GetMapping("/{id}/comments")
    public CommentsDto getComments(@PathVariable Integer id) {
        return commentService.getComments(id);
    }

    @Operation(summary = "Add comment")
    @PostMapping("/{id}/comments")
    public CommentDto addComment(
            @PathVariable Integer id,
            @RequestBody CreateOrUpdateCommentDto dto
    ) {
        return commentService.createComment(id, dto);
    }

    @Operation(summary = "Update comment")
    @PatchMapping("/{adId}/comments/{commentId}")
    public CommentDto updateComment(
            @PathVariable Integer adId,
            @PathVariable Integer commentId,
            @RequestBody CreateOrUpdateCommentDto dto
    ) {
        return commentService.updateComment(adId, commentId, dto);
    }

    @Operation(summary = "Delete comment")
    @DeleteMapping("/{adId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Integer adId,
            @PathVariable Integer commentId
    ) {

        commentService.deleteComment(adId, commentId);

    }

}
