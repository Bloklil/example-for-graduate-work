package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;

@RestController
@RequestMapping("/ads")
public class CommentController {

    @Operation(summary = "Get comments")
    @GetMapping("/{id}/comments")
    public CommentsDto getComments(@PathVariable Integer id) {
        return new CommentsDto();
    }

    @Operation(summary = "Add comment")
    @PostMapping("/{id}/comments")
    public CommentDto addComment(
            @PathVariable Integer id,
            @RequestBody CreateOrUpdateCommentDto dto
    ) {
        return new CommentDto();
    }

    @Operation(summary = "Update comment")
    @PatchMapping("/{adId}/comments/{commentId}")
    public CommentDto updateComment(
            @PathVariable Integer adId,
            @PathVariable Integer commentId,
            @RequestBody CreateOrUpdateCommentDto dto
    ) {
        return new CommentDto();
    }

    @Operation(summary = "Delete comment")
    @DeleteMapping("/{adId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Integer adId,
            @PathVariable Integer commentId
    ) {
    }

}
