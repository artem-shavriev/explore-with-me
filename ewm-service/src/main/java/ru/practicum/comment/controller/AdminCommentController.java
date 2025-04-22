package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoUpdate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Validated
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentStatusByAdmin(@PathVariable Integer commentId,
                                                 @RequestBody CommentDtoUpdate commentDtoUpdate) {

        return commentService.updateStatusByAdmin(commentId, commentDtoUpdate);
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentByAdmin(@PathVariable Integer commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
