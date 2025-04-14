package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(defaultValue = "") String text,
                                         @RequestParam(required = false) List<Integer> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                         @RequestParam(defaultValue = "0") Integer from,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         HttpServletRequest request) {

        eventService.addHit(request.getRequestURI(), request.getRemoteAddr());

        /*if (rangeStart != null && rangeEnd != null) {
            return eventService.getEventsWithTimeRange(text, categories, paid, rangeStart,
                    rangeEnd, onlyAvailable, sort, from, size);
        } else {
            return eventService.getEvents(text, categories, paid, onlyAvailable, sort, from, size);
        }*/
        return eventService.getEventsWithTimeRange(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Integer id, HttpServletRequest request) {
        eventService.addHit(request.getRequestURI(), request.getRemoteAddr());
        eventService.setViews(id, request.getRequestURI());
        return eventService.getEventById(id);
    }
}
