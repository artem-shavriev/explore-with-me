package ru.practicum.event;

import org.springframework.stereotype.Service;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Service
public interface EventService {
    List<EventShortDto> getEventsWithTimeRange(String text,
                                  List<Integer> categories,
                                  Boolean paid,
                                  String rangeStart,
                                  String rangeEnd, Boolean onlyAvailable,
                                  String sort,
                                  Integer from,
                                  Integer size);

    List<EventShortDto> getEventsRange(String text,
                                               List<Integer> categories,
                                               Boolean paid,
                                               Boolean onlyAvailable,
                                               String sort,
                                               Integer from,
                                               Integer size);

    void addHit(String uri, String ip);
}
