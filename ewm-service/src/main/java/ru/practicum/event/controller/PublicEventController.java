package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam String text,
                                         @RequestParam List<Integer> categories,
                                         @RequestParam Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam String sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {

        eventService.addHit(request.getRequestURI(), request.getRemoteAddr());

        if (rangeStart != null && rangeEnd != null) {
            return eventService.getEventsWithTimeRange(text, categories, paid, rangeStart,
                    rangeEnd, onlyAvailable, sort, from, size);
        } else {
           return eventService.getEventsRange(text, categories, paid, onlyAvailable, sort, from, size);
        }
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById()
}
