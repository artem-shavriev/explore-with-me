package ru.practicum.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentDtoUpdate;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.participation.ParticipationRequestRepository;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.participation.model.Status;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final ParticipationRequestRepository participationRequestRepository;

    public CommentDto addComment(Integer eventId, Integer userId, NewCommentDto commentDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие для комментирования не найдено"));

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Комментатор не найден."));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Создатель события не может оставить к нему комментарий.");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("На событие нельзя оставить комментарий так как оно не опубликовано.");
        }

        if (participationRequestRepository.findByRequesterIdAndEventId(userId, eventId).isEmpty()) {
            throw new NotFoundException("Пользователь не может оставить коментарий к событию на участие" +
                    "в котором он не подавал заявку.");
        }

        List<ParticipationRequest> participationRequest = participationRequestRepository
                .findByRequesterIdAndEventId(userId, eventId);

        if (!participationRequest.getFirst().getStatus().equals(Status.CONFIRMED)) {
            throw new ValidationException("Пользователь не может оставить комменатрий так как его запрос " +
                    "на участвие в мероприятии не подтвержден.");
        }

        Comment comment = commentMapper.newCommentDtoToComment(commentDto, userId, eventId);
        comment.setStatus(CommentStatus.PENDING);
        comment.setCreated(LocalDateTime.now());

        comment = commentRepository.save(comment);

        log.info("Коментарий добавлен а событие с id {} пользователем с id {}", eventId, userId);
        return commentMapper.mapToCommentDto(comment);
    }

    public CommentDto updateComment(Integer userId, Integer commentId, CommentDtoUpdate commentDtoUpdate) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с данным id не существует."));

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Комментатор не найден."));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Пользователь не может изменить коментарий" +
                    " так как не является его создателем.");
        }

        if (comment.getStatus().equals(CommentStatus.PUBLISHED)) {
            throw new ValidationException("Нельжя изменить комменатрий так как он уже опубликован.");
        }

        if (commentDtoUpdate.hasText()) {
            comment.setText(commentDtoUpdate.getText());
        }

        comment = commentRepository.save(comment);

        log.info("Коментарий с id {} обновлен", commentId);
        return commentMapper.mapToCommentDto(comment);
    }

    public List<CommentDto> getUsersComment(Integer authorId) {
        userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        List<Comment> comments = commentRepository.findAllByAuthorId(authorId);

        log.info("Коментарии пользователя с id {} получены.", authorId);
        return commentMapper.mapToCommentDto(comments);
    }

    public List<CommentDto> getEventComment(Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Нельзя просмотреть комменатрии на неопубликованное событие.");
        }

        List<Comment> comments = commentRepository
                .findByEventIdAndStatus(eventId, CommentStatus.PUBLISHED);

        log.info("Комментарии события с id {} получены.", eventId);
        return commentMapper.mapToCommentDto(comments);
    }

    public void deleteCommentByUser(Integer userId, Integer commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь удаляющий комментарий не найден."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с данным id не существует."));
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Нельзя удалить не свой комментарий.");
        }

        log.info("Коментарий с id {}, удален пользователем с id {}", commentId, userId);
        commentRepository.deleteById(commentId);
    }

    public void deleteCommentByAdmin(Integer commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с данным id не существует."));

        if (!comment.getStatus().equals(CommentStatus.PUBLISHED)) {
            throw new ValidationException("Администратор не может удалить неопубликованный комментарий.");
        }

        log.info("Комментарий с id {} удален администратором.", commentId);
        commentRepository.deleteById(commentId);
    }

    public CommentDto updateStatusByAdmin(Integer commentId, CommentDtoUpdate commentDtoUpdate) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с данным id не существует."));

        if (!comment.getStatus().equals(CommentStatus.PENDING)) {
            throw new ValidationException("Изменить статус комментария нельзя. Так как статус не PENDING");
        }

        try {
        CommentStatus status = CommentStatus.valueOf(commentDtoUpdate.getStatus());

        comment.setStatus(status);
        comment = commentRepository.save(comment);


        log.info("Статус комментария с id {} обновлен администратором на {}", commentId, status);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка парсинга Status");
        }

        return commentMapper.mapToCommentDto(comment);
    }
}

