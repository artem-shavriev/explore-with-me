package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllByCategoryOrderByEventDateDesc(Integer categoryId);

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE e.id IN :idList " +
            "ORDER BY e.eventDate DESC")
    List<Event> findByIdList(@Param("idList") List<Integer> idList);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = :state " +
            "AND (lower(e.description) LIKE lower(concat('%', :text, '%')) " +
            "OR (lower(e.annotation) LIKE lower(concat('%', :text, '%')))) " +
            "AND e.category IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND (e.participantLimit = 0 OR e.confirmedRequests <= e.participantLimit)" +
            "ORDER BY e.eventDate")
    Page<Event> getAvailableEventsWithTimeRangeSortEventDate(@Param("state") String state,
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
            "AND e.category IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "ORDER BY e.eventDate")
    Page<Event> getEventsWithTimeRangeSortEventDate(@Param("state") String state,
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
            "AND e.category IN :categories " +
            "AND e.paid = :paid " +
            "AND (e.participantLimit = 0 OR e.confirmedRequests <= e.participantLimit)" +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "ORDER BY e.views")
    Page<Event> getAvailableEventsWithTimeRangeSortByViews(@Param("state") String state,
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
            "AND e.category IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "ORDER BY e.views")
    Page<Event> getEventsWithTimeRangeSortByViews(@Param("state") String state,
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
            "AND e.category IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :now " +
            "AND (e.participantLimit = 0 OR e.confirmedRequests <= e.participantLimit)" +
            "ORDER BY e.eventDate")
    Page<Event> getAvailableEventsSortByEventDate(@Param("state") String state,
                                                             @Param("text") String text,
                                                             @Param("categories") List<Integer> categories,
                                                             @Param("paid") Boolean paid,
                                                             @Param("now") LocalDateTime now,
                                                             Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = :state " +
            "AND (lower(e.description) LIKE lower(concat('%', :text, '%')) " +
            "OR (lower(e.annotation) LIKE lower(concat('%', :text, '%')))) " +
            "AND e.category IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :now " +
            "ORDER BY e.eventDate")
    Page<Event> getEventsSortEventByDate(@Param("state") String state,
                                                  @Param("text") String text,
                                                  @Param("categories") List<Integer> categories,
                                                  @Param("paid") Boolean paid,
                                                  @Param("now") LocalDateTime now,
                                                  Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = :state " +
            "AND (lower(e.description) LIKE lower(concat('%', :text, '%')) " +
            "OR (lower(e.annotation) LIKE lower(concat('%', :text, '%')))) " +
            "AND e.category IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :now " +
            "AND (e.participantLimit = 0 OR e.confirmedRequests <= e.participantLimit)" +
            "ORDER BY e.views")
    Page<Event> getAvailableEventsSortByViews(@Param("state") String state,
                                                  @Param("text") String text,
                                                  @Param("categories") List<Integer> categories,
                                                  @Param("paid") Boolean paid,
                                                  @Param("now") LocalDateTime now,
                                                  Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = :state " +
            "AND (lower(e.description) LIKE lower(concat('%', :text, '%')) " +
            "OR (lower(e.annotation) LIKE lower(concat('%', :text, '%')))) " +
            "AND e.category IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate > :now " +
            "ORDER BY e.views")
    Page<Event> getEventsSortByViews(@Param("state") String state,
                                              @Param("text") String text,
                                              @Param("categories") List<Integer> categories,
                                              @Param("paid") Boolean paid,
                                              @Param("now") LocalDateTime now,
                                              Pageable pageable);
}



/*
@Query("SELECT new ru.practicum.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip) as hits) " +
        "FROM Hit h " +
        "WHERE h.uri IN :uris " +
        "AND h.timestamp BETWEEN :start AND :end " +
        "GROUP BY h.app, h.uri order by hits desc")
List<ViewStats> findUniqueStatsByUrisInPeriod(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        @Param("uris") List<String> uris
);*/
