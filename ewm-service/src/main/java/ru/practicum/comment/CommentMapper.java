package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoUpdate;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public Comment newCommentDtoToComment(NewCommentDto commentDto, Integer userId, Integer eventId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя не существует."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События не существует."));

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
