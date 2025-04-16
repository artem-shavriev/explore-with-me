package ru.practicum.participation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.participation.model.Status;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ParticipationService {
    private final ParticipationRequestRepository participationRepository;
    private final ParticipationMapper participationMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public List<ParticipationRequestDto> getParticipationByRequester(Integer requesterId) {
        return participationMapper.mapToDto(participationRepository.findAllByRequester(requesterId));
    }

    public ParticipationRequestDto addParticipation(Integer requesterId, Integer eventId) {
        if (!participationRepository.findAllByRequesterAndEvent(requesterId, eventId).isEmpty()) {
            throw new ConflictException("Такой запрос уже существует.");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователь не наден."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События в запросе не существует."));

        if (event.getInitiator().getId().equals(requesterId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии.");
        }

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ConflictException("У события достигнут лимит запросов на участие.");
        }


        ParticipationRequest participationRequest = ParticipationRequest.builder().requester(requester)
                .event(event).created(LocalDateTime.now()).build();
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            participationRequest.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            participationRequest.setStatus(Status.PENDING);
        }

        participationRequest = participationRepository.save(participationRequest);

        return participationMapper.mapToDto(participationRequest);
    }

    public ParticipationRequestDto cancelRequest(Integer requesterId, Integer requestId) {
        ParticipationRequest participationRequest = participationRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Отменяемый запрос не найден."));

        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователь в запросе не существует."));

        if (!participationRequest.getRequester().getId().equals(requesterId)) {
            throw new ConflictException("Пользовтель не является владельцем запроса.");
        }

        if (participationRequest.getStatus().equals(Status.CONFIRMED)) {
            throw new ConflictException("Нельзя отменить уже принятую заявку.");
        }

        participationRequest.setStatus(Status.CANCELED);
        participationRequest = participationRepository.save(participationRequest);

        return participationMapper.mapToDto(participationRequest);
    }

    public List<ParticipationRequestDto> findParticipationsByIdList(List<Integer> idList) {
        return participationMapper.mapToDto(participationRepository.findParticipationByIdIn(idList));
    }

    public List<ParticipationRequestDto> getUserRequests(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));
        log.info("Список запросов пользователя получен");
        return getParticipationByRequester(userId);
    }

    public List<ParticipationRequestDto> getParticipationRequests(Integer requesterId, Integer eventId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));
        if (!event.getInitiator().getId().equals(requesterId)) {
            throw new ForbiddenException("Событие созданно другим пользователем. ");
        }

        return participationMapper.mapToDto(participationRepository.findAllByEvent(eventId));
    }

    public EventRequestStatusUpdateResult updateRequestsStatus(Integer userId, Integer eventId,
                                                               EventRequestStatusUpdateRequest statusUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));
        if (!event.getInitiator().getId().equals(userId)) {
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

        List<ParticipationRequestDto> participationRequestsDtoList = findParticipationsByIdList(statusUpdateRequest
                .getRequestIds());

        String updateStatus = statusUpdateRequest.getStatus();

        if (updateStatus.equals(Status.CONFIRMED.toString())) {
            for (ParticipationRequestDto request : participationRequestsDtoList) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    if (request.getStatus().equals(Status.PENDING.toString())) {
                        request.setStatus(updateStatus);
                        participationRepository.save(participationMapper.dtoToMap(request, user, event));
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        eventRepository.save(event);
                        confirmedParticipationRequests.add(request);
                    }
                } else {
                    if (request.getStatus().equals(Status.PENDING.toString())) {
                        request.setStatus(Status.REJECTED.toString());
                        participationRepository.save(participationMapper.dtoToMap(request, user, event));
                        rejectedParticipationRequests.add(request);
                    }
                }
            }
        } else if (updateStatus.equals(Status.REJECTED.toString())) {
            for (ParticipationRequestDto request : participationRequestsDtoList) {
                if (request.getStatus().equals(Status.PENDING.toString())) {
                    request.setStatus(updateStatus);
                    participationRepository.save(participationMapper.dtoToMap(request, user, event));
                    rejectedParticipationRequests.add(request);
                }
            }
        }

        eventRequestStatusUpdateResult.setConfirmedRequests(confirmedParticipationRequests);
        eventRequestStatusUpdateResult.setRejectedRequests(rejectedParticipationRequests);

        return eventRequestStatusUpdateResult;
    }
}
