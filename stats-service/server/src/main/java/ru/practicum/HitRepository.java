package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.hit.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Integer> {
    @Query("SELECT h.app, h.uri, COUNT(DISTINCT h.ip) as hits " +
            "FROM Hit h " +
            "WHERE h.uri IN :uris " +
            "AND h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri order by hits desc")
    List<Object[]> findUniqueStatsByUrisInPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT h.app, h.uri, COUNT(h) as hits " +
            "FROM Hit h " +
            "WHERE h.uri IN :uris " +
            "AND h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri order by hits desc")
    List<Object[]> findStatsByUrisInPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") List<String> uris
    );

    @Query("SELECT h.app, h.uri, COUNT(h) as hits " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri order by hits desc")
    List<Object[]> findStatsInPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT h.app, h.uri, COUNT(DISTINCT h.ip) as hits " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri order by hits desc")
    List<Object[]> findUniqueStatsInPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
