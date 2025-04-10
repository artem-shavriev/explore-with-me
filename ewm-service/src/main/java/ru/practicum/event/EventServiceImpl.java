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
import ru.practicum.participation.ParticipationService;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.model.Status;
import ru.practicum.user.UserRepository;

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
    private final EventMapper eventMapper;
    private final ParticipationService participationService;
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
    public EventFullDto getEventById(Integer id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Событие не найдено."));

        if (!event.getState().equals(State.PUBLISHED.toString())) {
            throw new ConflictException("Это событие еще не опубликовано.");
        }

        Integer addView = event.getViews() + 1;
        event.setViews(addView);

        return eventMapper.eventToFullDto(event);
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
        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

        Page<Event> events = eventRepository
                .getEventsByAdminSortByViews(users, states, categories, start, end, eventPage);

        log.info("Список событий по запросу администратора получен.");
        return events.stream().map(eventMapper::eventToFullDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Integer eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие не найдено."));

        if (event.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ForbiddenException("Дата начала изменяемого события должна быть " +
                    "не ранее чем за час от даты публикации.");
        }

        if (updateRequest.hasStateAction()) {
            String stateAction = updateRequest.getStateAction();

            if (stateAction.equals(StateAction.PUBLISH_EVENT.toString())
                    && event.getState().equals(State.PUBLISHED.toString())) {
                throw new ForbiddenException("Cобытие можно публиковать, " +
                        "только если оно в состоянии ожидания публикации.");
            }

            if (stateAction.equals(StateAction.REJECT_EVENT.toString())
                    && event.getState().equals(State.PUBLISHED.toString())) {
                throw new ForbiddenException("Cобытие можно отклонить, только если оно еще не опубликовано");
            }

            event.setState(stateActionToState(updateRequest.getStateAction()));
        }

        if (updateRequest.hasAnnotation()) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.hasCategory()) {
            event.setCategory(updateRequest.getCategory().getId());
        }

        if (updateRequest.hasDescription()) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.hasEventDate()) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateRequest.getEventDate(), formatter);
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
        return eventMapper.eventToFullDto(event);
    }

    @Override
    @Transactional
    public List<EventShortDto> getEventsByUser(Integer userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));

        Pageable eventPage = PageRequest.of(from, size);

        Page<Event> events = eventRepository.findAllByInitiatorOrderByViews(userId, eventPage);

        log.info("Список событий по запросуп пользовтеля получен.");
        return events.stream().map(eventMapper::eventToShortDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Integer userId, NewEventDto newEventDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));

        LocalDateTime newEventDate = LocalDateTime.parse(newEventDto.getEventDate(), formatter);
        if (newEventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }

        Event event = eventMapper.newEventDtoToEvent(newEventDto);
        event.setCreatedOn(LocalDateTime.now());
        event = eventRepository.save(event);

        return eventMapper.eventToFullDto(event);
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

        return eventMapper.eventToFullDto(event);
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
            throw new ForbiddenException("Событие созданно другим пользователем. ");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }

        if (event.getState().equals(State.PUBLISHED.toString())) {
            throw new ForbiddenException("Изменить можно только отмененные события или " +
                    "события в состоянии ожидания модерации ");
        }

        if (updateRequest.hasStateAction()) {
            event.setState(stateActionToState(updateRequest.getStateAction()));
        }

        if (updateRequest.hasAnnotation()) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.hasCategory()) {
            event.setCategory(updateRequest.getCategory().getId());
        }

        if (updateRequest.hasDescription()) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.hasEventDate()) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateRequest.getEventDate(), formatter);
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
        return eventMapper.eventToFullDto(event);
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

    @Override
    @Transactional
    public void addHit(String uri, String ip) {
        String timestamp = LocalDateTime.now().format(formatter);
        EndpointHit endpointHit = EndpointHit.builder().app("ewm-main-service").ip(ip)
                .uri(uri).timestamp(timestamp).build();
        hitClient.addHit(endpointHit);
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

