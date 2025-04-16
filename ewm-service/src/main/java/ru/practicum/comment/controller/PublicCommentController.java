package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {
    private final CommentService commentService;

    public List<CommentDto> comments(@PathVariable Integer eventId) {
        return commentService.getEventComment(eventId);
    }
}
