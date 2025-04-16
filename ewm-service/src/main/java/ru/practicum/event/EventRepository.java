package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("SELECT e FROM Event e " +
            "WHERE e.category.id = :categoryId " +
            "ORDER BY e.eventDate DESC")
    List<Event> findAllByCategoryIdOrderByEventDateDesc(@Param("categoryId") Integer categoryId);

    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id = :initiatorId ")
    Page<Event> findAllByInitiatorId(@Param("initiatorId") Integer initiatorId, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.id IN :idList " +
            "ORDER BY e.eventDate DESC")
    List<Event> findByIdList(@Param("idList") List<Integer> idList);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = :state " +
            "AND (lower(e.description) LIKE lower(concat('%', :text, '%')) " +
            "OR (lower(e.annotation) LIKE lower(concat('%', :text, '%')))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND (e.participantLimit = 0 OR e.confirmedRequests <= e.participantLimit)" +
            "ORDER BY e.eventDate")
    Page<Event> getAvailableEventsWithTimeRangeSortEventDate(@Param("state") State state,
                                                             @Param("text") String text,
                                                             @Param("categories") List<Integer> categories,
                                                             @Param("paid") Boolean paid,
                                                             @Param("rangeStart") LocalDateTime rangeStart,
                                                             @Param("rangeEnd") LocalDateTime rangeEnd,
                                                             Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = :state " +
            "AND (lower(e.description) LIKE lower(concat('%', :text, '%')) " +
            "OR (lower(e.annotation) LIKE lower(concat('%', :text, '%')))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "ORDER BY e.eventDate")
    Page<Event> getEventsWithTimeRangeSortEventDate(@Param("state") State state,
                                                    @Param("text") String text,
                                                    @Param("categories") List<Integer> categories,
                                                    @Param("paid") Boolean paid,
                                                    @Param("rangeStart") LocalDateTime rangeStart,
                                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                                    Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "ORDER BY e.eventDate")
    Page<Event> getEventsByAdminSortByDate(@Param("users") List<Integer> users,
                                            @Param("states") List<State> states,
                                            @Param("categories") List<Integer> categories,
                                            @Param("rangeStart") LocalDateTime rangeStart,
                                            @Param("rangeEnd") LocalDateTime rangeEnd,
                                            Pageable pageable);
}
