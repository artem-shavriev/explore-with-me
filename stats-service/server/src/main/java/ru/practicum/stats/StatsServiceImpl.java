package ru.practicum.stats;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.HitRepository;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final HitRepository repository;

    @Override
    @Transactional
    public List<ViewStats> getStatsUri(String start, String end, Boolean unique, List<String> uris) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime timeStart = LocalDateTime.parse(start, formatter);
        LocalDateTime timeEnd = LocalDateTime.parse(end, formatter);
        if (timeStart.isAfter(timeEnd)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Дата начала не может быть позже даты окончания"
            );
        }

        if (unique) {
            log.info("Получена статистика по уникальны ip запросам и списку url.");
            return repository.findUniqueStatsByUrisInPeriod(timeStart, timeEnd, uris);

        } else {
            log.info("Получена статистика по всем ip запросам  и списку url.");
            return repository.findStatsByUrisInPeriod(timeStart, timeEnd, uris);
        }
    }

    @Override
    @Transactional
    public List<ViewStats> getStats(String start, String end, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime timeStart = LocalDateTime.parse(start, formatter);
        LocalDateTime timeEnd = LocalDateTime.parse(end, formatter);
        if (timeStart.isAfter(timeEnd)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Дата начала не может быть позже даты окончания"
            );
        }

        if (unique) {
            log.info("Получена статистика по уникальным ip запросам и всем url.");
            return repository.findUniqueStatsInPeriod(timeStart, timeEnd);

        } else {
            log.info("Получена статистика по всем ip запросам и всем url.");
            return repository.findStatsInPeriod(timeStart, timeEnd);
        }
    }
}

