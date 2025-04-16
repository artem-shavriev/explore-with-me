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

    public List<ParticipationRequestDto> getParticipationByEvent(Integer eventId) {
        return participationMapper.mapToDto(participationRepository.findAllByEvent(eventId));
    }

    public ParticipationRequestDto addParticipation(Integer requesterId, Integer eventId) {
        if (!participationRepository.findAllByRequesterAndEvent(requesterId, eventId).isEmpty()) {
            throw new ConflictException("Такой запрос уже существует.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События в запросе не существует."));

        if (event.getInitiator().equals(requesterId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (!event.getState().equals(State.PUBLISHED.toString())) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии.");
        }

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ConflictException("У события достигнут лимит запросов на участие.");
        }


        ParticipationRequest participationRequest = ParticipationRequest.builder().requester(requesterId)
                .event(eventId).created(LocalDateTime.now()).build();
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            participationRequest.setStatus(Status.CONFIRMED.toString());
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            participationRequest.setStatus(Status.PENDING.toString());
        }

        participationRequest = participationRepository.save(participationRequest);

        return participationMapper.mapToDto(participationRequest);
    }

    public ParticipationRequestDto cancelRequest(Integer requesterId, Integer requestId) {
        ParticipationRequest participationRequest = participationRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Отменяемый запрос не найден."));

        if (!participationRequest.getRequester().equals(requesterId)) {
            throw new ConflictException("Пользовтель не является владельцем запроса.");
        }

        if (participationRequest.getStatus().equals(Status.CONFIRMED.toString())) {
            throw new ConflictException("Нельзя отменить уже принятую заявку.");
        }

        participationRequest.setStatus(Status.CANCELED.toString());
        participationRequest = participationRepository.save(participationRequest);

        return participationMapper.mapToDto(participationRequest);
    }

    public List<ParticipationRequestDto> findParticipationsByIdList(List<Integer> idList) {
        return participationMapper.mapToDto(participationRepository.findParticipationsByIdList(idList));
    }

    /*public List<ParticipationRequestDto> getParticipationRequests(Integer userId, Integer eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с данным id не существует."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено."));
        if (!event.getInitiator().equals(userId)) {
            throw new ForbiddenException("Событие созданно другим пользователем. ");
        }

        return getParticipationByEvent(eventId);
    }

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

        List<ParticipationRequestDto> participationRequestsDtoList = findParticipationsByIdList(statusUpdateRequest
                .getRequestIds());

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
    }*/
}
