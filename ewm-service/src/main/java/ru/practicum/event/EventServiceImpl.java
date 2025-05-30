package ru.practicum.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.RequestParamsDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final HitClient hitClient;
    private final StatsClient statsClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public List<EventShortDto> getEvents(RequestParamsDto dto) {
        LocalDateTime start;
        LocalDateTime end;

        Pageable eventPage = PageRequest.of(dto.getFrom(), dto.getSize(),
                Sort.by(Sort.Direction.ASC, "eventDate"));

        Page<Event> events;
        Specification<Event> spec = Specification.where(null);

        if (dto.getText() != null) spec = spec.and(EventSpecification.hasText(dto.getText()));

        if (dto.getCategories() != null) spec = spec.and(EventSpecification.hasCategories(dto.getCategories()));

        if (dto.getPaid() != null) spec = spec.and(EventSpecification.hasPaid(dto.getPaid()));

        if (dto.getRangeStart() != null && dto.getRangeEnd() != null) {
            start = LocalDateTime.parse(dto.getRangeStart(), formatter);
            end = LocalDateTime.parse(dto.getRangeEnd(), formatter);

            if (start.isAfter(end)) {
                throw new ValidationException("Дата начала не может быть после конца.");
            }

            spec = spec.and(EventSpecification.dateBetween(start, end));
        } else {
            start = LocalDateTime.now();
            spec = spec.and(EventSpecification.dateAfterNow(start));
        }

        spec = spec.and(EventSpecification.hasState(State.PUBLISHED));

        if (dto.getOnlyAvailable() != null) spec = spec.and(EventSpecification.available(dto.getOnlyAvailable()));

        events = eventRepository.findAll(spec, eventPage);
        log.info("Список событий получен. Выборка по веремни. Сортеровка по дате.");

        addHit(dto.getUri(), dto.getIp());
        return setViewsForShortDto(events.getContent());
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Integer id, String uri, String ip) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Событие не найдено."));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Это событие еще не опубликовано.");
        }

        addHit(uri, ip);

        log.info("Событие получено по id {}", id);
        return setViewsForFullDto(event);
    }

    @Override
    @Transactional
    public List<EventFullDto> getEventsByAdmin(List<Integer> users,
                                               List<State> states,
                                               List<Integer> categories,
                                               String rangeStart,
                                               String rangeEnd,
                                               Integer from,
                                               Integer size) {

        LocalDateTime start;
        LocalDateTime end;
        Pageable eventPage = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "eventDate"));

        Page<Event> events;
        Specification<Event> spec = Specification.where(null);

        if (categories != null) spec = spec.and(EventSpecification.hasCategories(categories));

        if (states != null) spec = spec.and(EventSpecification.hasStates(states));

        if (users != null) spec = spec.and(EventSpecification.hasUsers(users));

        if (rangeStart != null && rangeEnd != null) {
            start = LocalDateTime.parse(rangeStart, formatter);
            end = LocalDateTime.parse(rangeEnd, formatter);

            if (start.isAfter(end)) {
                throw new ValidationException("Дата начала не может быть после конца.");
            }

            spec = spec.and(EventSpecification.dateBetween(start, end));
        } else {
            start = LocalDateTime.now();
            spec = spec.and(EventSpecification.dateAfterNow(start));
        }

        events = eventRepository.findAll(spec, eventPage);

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
                    && event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Cобытие можно публиковать, " +
                        "только если оно в состоянии ожидания публикации.");
            }

            if (stateAction.equals(StateAction.REJECT_EVENT.toString())
                    && event.getState().equals(State.PUBLISHED)) {
                throw new ConflictException("Cобытие можно отклонить, только если оно еще не опубликовано");
            }

            if (stateAction.equals(StateAction.PUBLISH_EVENT.toString())
                    && event.getState().equals(State.CANCELED)) {
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

        Page<Event> events = eventRepository.findAllByInitiatorId(userId, eventPage);

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

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Такой категории нет."));

        Event event = eventMapper.newEventDtoToEvent(newEventDto, category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);

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

        if (!event.getInitiator().getId().equals(userId)) {
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
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Событие созданно другим пользователем. ");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }

        if (event.getState().equals(State.PUBLISHED)) {
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

    public Map<String, Long> getMapOfViewsAndEventUri(List<String> uris) {
        LocalDateTime start = LocalDateTime.now().minusYears(5);
        LocalDateTime end = LocalDateTime.now();
        Map<String, Long> eventsViewsMap = new HashMap<>();

        try {
            ResponseEntity<List<ViewStats>> response = statsClient
                    .getStatsUri(start.format(formatter), end.format(formatter), true, uris);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {

                List<ViewStats> stats = response.getBody();

                stats.forEach(stat -> {
                    eventsViewsMap.put(stat.getUri(), stat.getHits());
                });

                return eventsViewsMap;
            } else {
                return eventsViewsMap;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<EventShortDto> setViewsForShortDto(List<Event> events) {
        List<EventShortDto> shortEvents = new ArrayList<>();
        List<String> uris = new ArrayList<>();

        events.forEach(event -> {
            uris.add("/events/" + event.getId());
        });

        Map<String, Long> mapOfViews = getMapOfViewsAndEventUri(uris);

        if (mapOfViews.isEmpty()) {
            for (Event event : events) {
                shortEvents.add(eventMapper.eventToShortDto(event, 0L));
            }
        } else {
            for (Event event : events) {
                String uri = "/events/" + event.getId();
                Long views = mapOfViews.get(uri);

                shortEvents.add(eventMapper.eventToShortDto(event, views));
            }
        }

        log.info("Просмотры события добавлены для списка short событий.");
        return shortEvents;
    }

    public List<EventFullDto> setViewsForFullDto(List<Event> events) {
        List<EventFullDto> fullEvents = new ArrayList<>();
        List<String> uris = new ArrayList<>();

        events.forEach(event -> {
            uris.add("/events/" + event.getId());
        });

        Map<String, Long> mapOfViews = getMapOfViewsAndEventUri(uris);

        if (mapOfViews.isEmpty()) {
            for (Event event : events) {
                fullEvents.add(eventMapper.eventToFullDto(event, 0L));
            }
        } else {
            for (Event event : events) {
                String uri = "/events/" + event.getId();
                Long views = mapOfViews.get(uri);

                fullEvents.add(eventMapper.eventToFullDto(event, views));
            }
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

    public State stateActionToState(String action) {
        if (action.equals(StateAction.PUBLISH_EVENT.toString())) {
            return State.PUBLISHED;
        } else if (action.equals(StateAction.REJECT_EVENT.toString())) {
            return State.CANCELED;
        } else if (action.equals(StateAction.CANCEL_REVIEW.toString())) {
            return State.CANCELED;
        } else if (action.equals(StateAction.SEND_TO_REVIEW.toString())) {
            return State.PENDING;
        } else {
            throw new NotFoundException("Неизвестное состояние StateAction");
        }
    }
}

