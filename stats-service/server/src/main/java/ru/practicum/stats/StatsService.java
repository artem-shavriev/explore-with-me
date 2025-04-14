package ru.practicum.stats;

import org.springframework.stereotype.Service;
import ru.practicum.ViewStats;

import java.util.ArrayList;
import java.util.List;

@Service
public interface StatsService {
    List<ViewStats> getStatsUri(String start, String end, Boolean unique, List<String> uris);

    List<ViewStats> getStats(String start, String end, Boolean unique);
}
