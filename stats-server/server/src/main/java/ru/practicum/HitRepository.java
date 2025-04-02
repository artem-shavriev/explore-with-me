package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.hit.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Integer> {
    @Query("SELECT DISTINCT h FROM Hit h " +
            "WHERE h.timestamp >= ?1 AND h.timestamp <= ?2 " +
            "AND (h.uri IN ?3)")
    List<Hit> findUniqueHitsByUrisInPeriod(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris);

    @Query("SELECT h FROM Hit h " +
            "WHERE h.timestamp >= ?1 AND h.timestamp <= ?2 " +
            "AND (h.uri IN ?3)")
    List<Hit> findHitsByUrisInPeriod(LocalDateTime start,
                                     LocalDateTime end,
                                     List<String> uris);

    @Query ("SELECT COUNT(h.app) FROM Hit h " +
            "WHERE h.app = ?1 AND h.uri = ?2 ORDER BY h.app")
    Integer hitCount(String app, String uri);
}
