package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Validated
public class AdminCommentController {
    private final CommentService commentService;

    public CommentDto updateCommentStatusByAdmin(@PathVariable Integer commentId,
                                                 @RequestParam(defaultValue = "CANCELED") String status) {

        return commentService.updateStatusByAdmin(commentId, status);
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentByUser(@PathVariable Integer commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
