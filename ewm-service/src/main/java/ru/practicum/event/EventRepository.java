package ru.practicum.event;

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
