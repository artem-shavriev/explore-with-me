package ru.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByEventIdAndStatus(Integer eventId, CommentStatus status);

    List<Comment> findAllByAuthorId(Integer authorId);
}