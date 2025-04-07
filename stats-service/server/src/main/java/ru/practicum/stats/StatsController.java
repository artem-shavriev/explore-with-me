package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ViewStats;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/stats")
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsService statsServiceImpl;

    @GetMapping
    public List<ViewStats> getStats(@RequestParam String start, @RequestParam String end,
                                    @RequestParam(defaultValue = "false") Boolean unique,
                                    @RequestParam(required = false) ArrayList<String> uris) {
        if (uris == null) {
            return statsServiceImpl.getStats(start, end, unique);
        } else {
            return statsServiceImpl.getStatsUri(start, end, unique, uris);
        }
    }
}
