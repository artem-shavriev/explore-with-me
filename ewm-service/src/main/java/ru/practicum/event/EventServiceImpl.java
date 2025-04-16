package ru.practicum.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.client.HitClient;
import ru.practicum.client.StatsClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.participation.ParticipationService;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.model.Status;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final ParticipationService participationService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final HitClient hitClient;
    private final StatsClient statsClient;

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
                                                      Integer size,
                                                      String uri,
                                                      String ip) {

        LocalDateTime start;
        LocalDateTime end;
        Pageable eventPage = PageRequest.of(from, size);
        String state = State.PUBLISHED.toString();
        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, formatter);
        } else {
            start = LocalDateTime.now().minusYears(10);
        }

        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            end = LocalDateTime.now().plusYears(10);
        }

        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть после конца.");
        }

        Page<Event> events;
        if (onlyAvailable) {
            events = eventRepository.getAvailableEventsWithTimeRangeSortEventDate(state, text,
                    categories, paid, start, end, eventPage);
        } else {
            events = eventRepository.getEventsWithTimeRangeSortEventDate(state, text,
                    categories, paid, start, end, eventPage);
        }
        log.info("Список событий получен. Выборка по веремни. Сортеровка по дате.");

        addHit(uri, ip);
        return setViewsForShortDto(events.getContent());
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Integer id, String uri, String ip) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Событие не найдено."));

        if (!event.getState().equals(State.PUBLISHED.toString())) {
            throw new NotFoundException("Это событие еще не опубликовано.");
        }

        addHit(uri, ip);

        log.info("Событие получено по id {}", id);
        return setViewsForFullDto(event);
    }

    @Override
    @Transactional
    public List<EventFullDto> getEventsByAdmin(List<Integer> users,
                                               List<String> states,
                                               List<Integer> categories,
                                               String rangeStart,
                                               String rangeEnd,
                                               Integer from,
                                               Integer size) {

        Pageable eventPage = PageRequest.of(from, size);
        LocalDateTime start;
        LocalDateTime end;

        if (rangeStart != null) {
            start = LocalDateTime.parse(rangeStart, formatter);
        } else {
            start = LocalDateTime.now().minusYears(10);
        }

        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            end = LocalDateTime.now().plusYears(10);
        }

        Page<Event> events = eventRepository
                .getEventsByAdminSortByDate(users, states, categories, start, end, eventPage);

        log.info("Список событий по запросу администратора получен.");
        return setViewsForFullDto(events.getContent());
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Integer eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено."));

        if (event.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ConflictException("Дата начала изменяемого события должна быть " +
                    "не ранее чем за час от даты публикации.");
        }

        if (updateRequest.hasStateAction()) {
            String stateAction = updateRequest.getStateAction();

            if (stateAction.equals(StateAction.PUBLISH_EVENT.toString())
                    && event.getState().equals(State.PUBLISHED.toString())) {
                throw new ConflictException("Cобытие можно публиковать, " +
                        "только если оно в состоянии ожидания публикации.");
            }

            if (stateAction.equals(StateAction.REJECT_EVENT.toString())
                    && event.getState().equals(State.PUBLISHED.toString())) {
                throw new ConflictException("Cобытие можно отклонить, только если оно еще не опубликовано");
            }

            if (stateAction.equals(StateAction.PUBLISH_EVENT.toString())
                    && event.getState().equals(State.CANCELED.toString())) {
                throw new ConflictException("Нельзя публиковать ранее отмененное событие.");
            }

            event.setState(stateActionToState(updateRequest.getStateAction()));
        }

        if (updateRequest.hasAnnotation()) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.hasCategory()) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категории с данным id {} не существует."));
            event.setCategory(category);
        }

        if (updateRequest.hasDescription()) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.hasEventDate()) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateRequest.getEventDate(), formatter);
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new ValidationException("Обновляемая дата события не должны быть наступившей.");
            }
            event.setEventDate(newEventDate);
        }

        if (updateRequest.hasLocation()) {
            Double lat = updateRequest.getLocation().getLat();
            Double lon = updateRequest.getLocation().getLon();
            List<Double> location = new ArrayList<>();
            location.add(lat);
            location.add(lon);
            event.setLocation(location);
        }

        if (updateRequest.hasPaid()) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.hasParticipantLimit()) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.hasRequestModeration()) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.hasTitle()) {
            event.setTitle(updateRequest.getTitle());
        }

        event = eventRepository.save(event);

        log.info("Событие обновлено администратором.");
        return setViewsForFullDto(event);
    }

    @Override
    @Transactional
    public List<EventShortDto> getEventsByUser(Integer userId, Integer from, Integer size, String uri) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));

        Pageable eventPage = PageRequest.of(from, size);

        Page<Event> events = eventRepository.findAllByInitiator(userId, eventPage);

        Long views = getViews(uri);

        log.info("Список событий по запросуп пользовтеля получен.");
        return events.stream().map(event -> eventMapper.eventToShortDto(event, views)).toList();
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Integer userId, NewEventDto newEventDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));

        LocalDateTime newEventDate = LocalDateTime.parse(newEventDto.getEventDate(), formatter);
        if (newEventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }

        Event event = eventMapper.newEventDtoToEvent(newEventDto);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING.toString());

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));

        event.setInitiator(initiator);
        event.setConfirmedRequests(0);
        event = eventRepository.save(event);

        log.info("Событие с id {} добавлено пользователем с id {}", event.getId(), userId);
        return setViewsForFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto getEventByUser(Integer userId, Integer eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));

        if (!event.getInitiator().equals(userId)) {
            throw new ForbiddenException("Событие созданно другим пользователем. ");
        }

        log.info("Событие с id {} получено пользователем с id {}.", eventId, userId);
        return setViewsForFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Integer userId,
                                          Integer eventId,
                                          UpdateEventUserRequest updateRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));
        if (!event.getInitiator().equals(userId)) {
            throw new ConflictException("Событие созданно другим пользователем. ");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }

        if (event.getState().equals(State.PUBLISHED.toString())) {
            throw new ConflictException("Изменить можно только отмененные события или " +
                    "события в состоянии ожидания модерации ");
        }

        if (updateRequest.hasStateAction()) {
            event.setState(stateActionToState(updateRequest.getStateAction()));
        }

        if (updateRequest.hasAnnotation()) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.hasCategory()) {
            event.setCategory(categoryMapper.dtoToMap(updateRequest.getCategory()));
        }

        if (updateRequest.hasDescription()) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.hasEventDate()) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateRequest.getEventDate(), formatter);
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new ValidationException("Обновляемая дата события не должны быть наступившей.");
            }
            event.setEventDate(newEventDate);
        }

        if (updateRequest.hasLocation()) {
            Double lat = updateRequest.getLocation().getLat();
            Double lon = updateRequest.getLocation().getLon();
            List<Double> location = new ArrayList<>();
            location.add(lat);
            location.add(lon);
            event.setLocation(location);
        }

        if (updateRequest.hasPaid()) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.hasParticipantLimit()) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.hasRequestModeration()) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.hasTitle()) {
            event.setTitle(updateRequest.getTitle());
        }

        event = eventRepository.save(event);

        log.info("Событие обновлено пользователем.");
        return setViewsForFullDto(event);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getParticipationRequests(Integer userId, Integer eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));
        if (!event.getInitiator().equals(userId)) {
            throw new ForbiddenException("Событие созданно другим пользователем. ");
        }

        return participationService.getParticipationByEvent(eventId);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatus(Integer userId, Integer eventId,
                                                               EventRequestStatusUpdateRequest statusUpdateRequest) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));
        if (!event.getInitiator().equals(userId)) {
            throw new ForbiddenException("Событие созданно другим пользователем. ");
        }

        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> confirmedParticipationRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedParticipationRequests = new ArrayList<>();

        if (event.getRequestModeration().equals(false) || event.getParticipantLimit().equals(0)) {
            confirmedParticipationRequests = getParticipationRequests(userId, eventId);
            eventRequestStatusUpdateResult.setConfirmedRequests(confirmedParticipationRequests);
            return eventRequestStatusUpdateResult;
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит одобренных заявок");
        }

        List<ParticipationRequestDto> participationRequestsDtoList = participationService
                .findParticipationsByIdList(statusUpdateRequest.getRequestIds());

        String updateStatus = statusUpdateRequest.getStatus();

        if (updateStatus.equals(Status.CONFIRMED.toString())) {
            for (ParticipationRequestDto request : participationRequestsDtoList) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    if (request.getStatus().equals(Status.PENDING.toString())) {
                        request.setStatus(updateStatus);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        confirmedParticipationRequests.add(request);
                    }
                } else {
                    if (request.getStatus().equals(Status.PENDING.toString())) {
                        request.setStatus(Status.REJECTED.toString());
                        rejectedParticipationRequests.add(request);
                    }
                }
            }
        } else if (updateStatus.equals(Status.REJECTED.toString())) {
            for (ParticipationRequestDto request : participationRequestsDtoList) {
                if (request.getStatus().equals(Status.PENDING.toString())) {
                    request.setStatus(updateStatus);
                    rejectedParticipationRequests.add(request);
                }
            }
        }

        eventRequestStatusUpdateResult.setConfirmedRequests(confirmedParticipationRequests);
        eventRequestStatusUpdateResult.setRejectedRequests(rejectedParticipationRequests);

        return eventRequestStatusUpdateResult;
    }

    private void addHit(String uri, String ip) {
        String timestamp = LocalDateTime.now().format(formatter);
        EndpointHit endpointHit = EndpointHit.builder().app("ewm-main-service").ip(ip)
                .uri(uri).timestamp(timestamp).build();
        hitClient.addHit(endpointHit);
    }

    public Long getViews(String uri) {
        LocalDateTime start = LocalDateTime.now().minusYears(5);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of(uri);

        try {
            ResponseEntity<List<ViewStats>> response = statsClient
                    .getStatsUri(start.format(formatter), end.format(formatter), true, uris);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                return response.getBody().getFirst().getHits();
            } else {
                return 0L;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<EventShortDto> setViewsForShortDto(List<Event> events) {
        List<EventShortDto> shortEvents = new ArrayList<>();

        for (Event event : events) {
            String uri = "/events/" + event.getId();
            Long views = getViews(uri);
            shortEvents.add(eventMapper.eventToShortDto(event, views));
        }

        log.info("Просмотры события добавлены для списка short событий.");
        return shortEvents;
    }

    public List<EventFullDto> setViewsForFullDto(List<Event> events) {
        List<EventFullDto> fullEvents = new ArrayList<>();

        for (Event event : events) {
            String uri = "/events/" + event.getId();
            Long views = getViews(uri);
            fullEvents.add(eventMapper.eventToFullDto(event, views));
        }

        log.info("Просмотры события добавлены для списка full событий.");
        return fullEvents;
    }

    public EventFullDto setViewsForFullDto(Event event) {
        String uri = "/events/" + event.getId();
        Long views = getViews(uri);

        log.info("Просмотры добавлены для событий с id {}.", event.getId());

        return eventMapper.eventToFullDto(event, views);
    }

    public String stateActionToState(String action) {
        if (action.equals(StateAction.PUBLISH_EVENT.toString())) {
            return State.PUBLISHED.toString();
        } else if (action.equals(StateAction.REJECT_EVENT.toString())) {
            return State.CANCELED.toString();
        } else if (action.equals(StateAction.CANCEL_REVIEW.toString())) {
            return State.CANCELED.toString();
        } else if (action.equals(StateAction.SEND_TO_REVIEW.toString())) {
            return State.PENDING.toString();
        } else {
            throw new NotFoundException("Неизвестное состояние StateAction");
        }
    }
}

