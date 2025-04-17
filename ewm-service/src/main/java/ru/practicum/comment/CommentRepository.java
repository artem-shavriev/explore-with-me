package ru.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c " +
            "WHERE c.event.id = :eventId " +
            "AND c.status = :status")
    List<Comment> findByEventIdAndStatus(@Param("eventId") Integer eventId, @Param("status") CommentStatus status);

    List<Comment> findAllByAuthorId(Integer authorId);
}