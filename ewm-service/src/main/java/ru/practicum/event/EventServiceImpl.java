package ru.practicum.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.client.HitClient;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final HitClient hitClient;

    @Override
    @Transactional
    public List<EventShortDto> getEventsWithTimeRange(String text,
                                                      List<Integer> categories,
                                                      Boolean paid,
                                                      String rangeStart,
                                                      String rangeEnd,
                                                      Boolean onlyAvailable,
                                                      String sort,
                                                      Integer from,
                                                      Integer size) {

        Pageable eventPage = PageRequest.of(from, size);
        String state = State.PUBLISHED.toString();
        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

        if (sort.equals("EVENT_DATE")) {
            Page<Event> events;
            if (onlyAvailable) {
                events = eventRepository.getAvailableEventsWithTimeRangeSortEventDate(state, text,
                        categories, paid, start, end, eventPage);
            } else {
                events = eventRepository.getEventsWithTimeRangeSortEventDate(state, text,
                        categories, paid, start, end, eventPage);
            }
            log.info("Список событий получен. Выборка по веремни. Сортеровка по дате.");
            return events.stream().map(eventMapper::eventToShortDto).toList();

        } else if (sort.equals("VIEWS")) {
            Page<Event> events;
            if (onlyAvailable) {
                events = eventRepository.getAvailableEventsWithTimeRangeSortByViews(state, text,
                        categories, paid, start, end, eventPage);
            } else {
                events = eventRepository.getEventsWithTimeRangeSortByViews(state, text,
                        categories, paid, start, end, eventPage);
            }
            log.info("Список событий получен. Выборка по веремни. Сортеровка по просмотрам.");
            return events.stream().map(eventMapper::eventToShortDto).toList();
        } else {
            throw new NotFoundException("Неизвестный параметр sort");
        }
    }

    @Override
    @Transactional
    public List<EventShortDto> getEventsRange(String text,
                                              List<Integer> categories,
                                              Boolean paid,
                                              Boolean onlyAvailable,
                                              String sort,
                                              Integer from,
                                              Integer size) {

        Pageable eventPage = PageRequest.of(from, size);
        String state = State.PUBLISHED.toString();
        LocalDateTime now = LocalDateTime.now();

        if (sort.equals("EVENT_DATE")) {
            Page<Event> events;
            if (onlyAvailable) {
                events = eventRepository.getAvailableEventsSortByEventDate(state, text,
                        categories, paid, now, eventPage);
            } else {
                events = eventRepository.getEventsSortEventByDate(state, text,
                        categories, paid, now, eventPage);
            }
            log.info("Список событий получен. Сортеровка по дате.");
            return events.stream().map(eventMapper::eventToShortDto).toList();

        } else if (sort.equals("VIEWS")) {
            Page<Event> events;
            if (onlyAvailable) {
                events = eventRepository.getAvailableEventsSortByViews(state, text,
                        categories, paid, now, eventPage);
            } else {
                events = eventRepository.getEventsSortByViews(state, text,
                        categories, paid, now, eventPage);
            }
            log.info("Список событий получен. Сортеровка по просмотрам.");
            return events.stream().map(eventMapper::eventToShortDto).toList();
        } else {
            throw new NotFoundException("Неизвестный параметр sort");
        }
    }

    @Override
    @Transactional
    public void addHit(String uri, String ip) {
        String timestamp = LocalDateTime.now().format(formatter);
        EndpointHit endpointHit = EndpointHit.builder().app("ewm-main-service").ip(ip)
                .uri(uri).timestamp(timestamp).build();
        hitClient.addHit(endpointHit);
    }
}

