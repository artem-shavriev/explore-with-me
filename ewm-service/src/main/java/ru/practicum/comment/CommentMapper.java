package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public Comment newCommentDtoToComment(NewCommentDto commentDto, User author, Event event) {

        return Comment.builder().text(commentDto.getText()).author(author).event(event).build();
    }

    public CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder().text(comment.getText())
                .authorName(comment.getAuthor().getName()).created(comment.getCreated()).build();
    }

    public List<CommentDto> mapToCommentDto(List<Comment> comments) {
        return comments.stream().map(this::mapToCommentDto).toList();
    }
}
