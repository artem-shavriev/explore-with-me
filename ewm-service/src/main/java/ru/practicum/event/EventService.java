package ru.practicum.event;

import org.springframework.stereotype.Service;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.participation.dto.ParticipationRequestDto;

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
                                               Integer size,
                                               String uri,
                                               String ip);

    EventFullDto getEventById(Integer id, String uri, String ip);

    List<EventFullDto> getEventsByAdmin(List<Integer> users,
                                 List<State> states,
                                 List<Integer> categories,
                                 String rangeStart,
                                 String rangeEnd,
                                 Integer from,
                                 Integer size);

    EventFullDto updateEventByAdmin(Integer eventId, UpdateEventAdminRequest updateRequest);

    List<EventShortDto> getEventsByUser(Integer userId, Integer from, Integer size, String uri);

    EventFullDto addEvent(Integer userId, NewEventDto newEventDto);

    EventFullDto getEventByUser(Integer userId, Integer eventId);

    EventFullDto updateEventByUser(Integer userId,
                                   Integer eventId,
                                   UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getParticipationRequests(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(Integer userId, Integer eventId,
                                                      EventRequestStatusUpdateRequest statusUpdateRequest);

    List<EventShortDto> setViewsForShortDto(List<Event> events);
}
