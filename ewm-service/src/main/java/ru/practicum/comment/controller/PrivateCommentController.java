package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoUpdate;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments/{userId}")
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    public CommentDto addComment(@PathVariable Integer eventId,
                                 @PathVariable Integer userId,
                                 @Valid  @RequestBody NewCommentDto commentDto) {

        return commentService.addComment(eventId, userId, commentDto);
    }

    @PatchMapping("{commentId}")
    public CommentDto updateComment(@PathVariable Integer userId,
                                    @PathVariable Integer commentId,
                                    @Valid  @RequestBody CommentDtoUpdate commentDtoUpdate) {

        return commentService.updateComment(userId, commentId, commentDtoUpdate);
    }

    @GetMapping
    public List<CommentDto> getUsersComment(@PathVariable Integer userId) {
        return commentService.getUsersComment(userId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteCommentByUser(@PathVariable Integer userId,
                                    @PathVariable Integer commentId) {

        commentService.deleteCommentByUser(userId, commentId);
    }
}
