package ru.practicum.participation;

import org.springframework.stereotype.Component;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.model.ParticipationRequest;

import java.util.List;

@Component
public class ParticipationMapper {
    public ParticipationRequestDto mapToDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId()).event(participationRequest.getEvent())
                .requester(participationRequest.getRequester())
                .status(participationRequest.getStatus()).build();
    }

    public List<ParticipationRequestDto> mapToDto(List<ParticipationRequest> participationRequests) {
        return participationRequests.stream().map(this::mapToDto).toList();
    }
}
