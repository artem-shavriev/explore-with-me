package ru.practicum.participation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.participation.model.Status;
import ru.practicum.user.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
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
        return participationMapper.mapToDto(participationRepository.findParticipationsByIdList(idList));
    }
}
