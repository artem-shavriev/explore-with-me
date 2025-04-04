package ru.practicum.stats;

import jakarta.transaction.Transactional;
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

    @Override
    @Transactional
    public List<ViewStats> getStatsUri(String start, String end, Boolean unique, ArrayList<String> uris) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime timeStart = LocalDateTime.parse(start, formatter);
        LocalDateTime timeEnd = LocalDateTime.parse(end, formatter);

        if (unique == true) {
            return repository.findUniqueStatsByUrisInPeriod(timeStart, timeEnd, uris);

        } else {
            return repository.findStatsByUrisInPeriod(timeStart, timeEnd, uris);
        }
    }

    @Override
    @Transactional
    public List<ViewStats> getStats(String start, String end, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime timeStart = LocalDateTime.parse(start, formatter);
        LocalDateTime timeEnd = LocalDateTime.parse(end, formatter);

        if (unique == true) {
            return repository.findUniqueStatsInPeriod(timeStart, timeEnd);

        } else {
            return repository.findStatsInPeriod(timeStart, timeEnd);
        }
    }

}
