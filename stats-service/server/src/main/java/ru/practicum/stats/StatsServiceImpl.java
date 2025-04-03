package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.HitRepository;
import ru.practicum.ViewStats;
import ru.practicum.hit.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final HitRepository repository;

    public List<ViewStats> getStats(String start, String end, Boolean unique, ArrayList<String> uris) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime timeStart = LocalDateTime.parse(start, formatter);
        LocalDateTime timeEnd = LocalDateTime.parse(end, formatter);
        List<Hit> hits = new ArrayList<>();

        /*if (unique == true) {
            hits = repository.findUniqueHitsByUrisInPeriod(timeStart, timeEnd, uris);

            List<ViewStats> statsList = new ArrayList<>();
            hits.forEach(hit -> {
                ViewStats viewStats = new ViewStats(hit.getApp(), hit.getUri(),
                        repository.countByAppAndUriAndIp(hit.getApp(), hit.getUri(), hit.getIp()));
                statsList.add(viewStats);
            });

            return statsList;
        } else {
            hits = repository.findHitsByUrisInPeriod(timeStart, timeEnd, uris);
            List<ViewStats> statsList = new ArrayList<>();
            hits.forEach(hit -> {
                ViewStats viewStats = new ViewStats(hit.getApp(), hit.getUri(),
                        repository.countByAppAndUri(hit.getApp(), hit.getUri()));
                statsList.add(viewStats);
            });
            return statsList;
        }*/
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
