package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.HitRepository;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final HitRepository repository;

    public List<ViewStats> getStats(String start, String end, Boolean unique, ArrayList<String> uris) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime timeStart = LocalDateTime.parse(start, formatter);
        LocalDateTime timeEnd = LocalDateTime.parse(end, formatter);

        if (unique == true) {
            List<Object[]> stats = repository.findUniqueStatsByUrisInPeriod(timeStart, timeEnd, uris);
            return stats.stream()
                    .map(row -> new ViewStats((String) row[0], (String) row[1], ((Number) row[2]).intValue())).toList();
        } else {
            List<Object[]> stats = repository.findStatsByUrisInPeriod(timeStart, timeEnd, uris);
            return stats.stream()
                    .map(row -> new ViewStats((String) row[0], (String) row[1], ((Number) row[2]).intValue())).toList();
        }
    }
}
