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

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ParticipationService {
    private final ParticipationRequestRepository participationRepository;
    private final ParticipationMapper participationMapper;
    private final EventRepository eventRepository;

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

        participationRequest.setStatus(Status.CANCELED.toString());
        participationRequest = participationRepository.save(participationRequest);

        return participationMapper.mapToDto(participationRequest);
    }

    public List<ParticipationRequestDto> findParticipationsByIdList(List<Integer> idList) {
        return participationMapper.mapToDto(participationRepository.findParticipationsByIdList(idList));
    }
}
