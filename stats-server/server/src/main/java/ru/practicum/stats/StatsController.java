package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/stats")
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsService statsServiceImpl;

    @GetMapping
    public List<StatsDtoResponse> getStats(@RequestParam String start, @RequestParam String end,
                                           @RequestParam Boolean unique, @RequestParam ArrayList<String> uris) {
        return statsServiceImpl.getStats(start, end, unique, uris);
    }
}
