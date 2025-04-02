package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.HitRepository;
import ru.practicum.hit.model.Hit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final HitRepository repository;

    public List<StatsDtoResponse> getStats(String start, String end, Boolean unique, ArrayList<String> uris) {
        LocalDateTime timeStart = LocalDateTime.parse(start);
        LocalDateTime timeEnd = LocalDateTime.parse(end);
        List<Hit> hits = new ArrayList<>();
        if (unique == true) {
            hits = repository.findUniqueHitsByUrisInPeriod(timeStart, timeEnd, uris);
        } else {
            hits = repository.findHitsByUrisInPeriod(timeStart, timeEnd, uris);
        }

        List<StatsDtoResponse> statsList = new ArrayList<>();
        hits.forEach(hit -> {
            StatsDtoResponse statsDtoResponse = new StatsDtoResponse(hit.getApp(), hit.getUri(),
                    repository.hitCount(hit.getApp(), hit.getUri()));
            statsList.add(statsDtoResponse);
        });

        return statsList;
    }
}
