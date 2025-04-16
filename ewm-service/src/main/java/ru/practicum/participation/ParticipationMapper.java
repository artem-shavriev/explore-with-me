package ru.practicum.participation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.participation.model.ParticipationRequest;
import ru.practicum.participation.model.Status;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class ParticipationMapper {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ParticipationRequestDto mapToDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId()).event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .created(participationRequest.getCreated().format(formatter))
                .status(participationRequest.getStatus().toString()).build();
    }

    public List<ParticipationRequestDto> mapToDto(List<ParticipationRequest> participationRequests) {
        return participationRequests.stream().map(this::mapToDto).toList();
    }

    public ParticipationRequest dtoToMap(ParticipationRequestDto participationRequestDto, User user, Event event) {
        return ParticipationRequest.builder()
                .id(participationRequestDto.getId())
                .requester(user)
                .event(event)
                .created(LocalDateTime.parse(participationRequestDto.getCreated(), formatter))
                .status(stringStatusToEnum(participationRequestDto.getStatus())).build();
    }

    public Status stringStatusToEnum(String string) {
        return switch (string) {
            case "CONFIRMED" -> Status.CONFIRMED;
            case "REJECTED" -> Status.REJECTED;
            case "CANCELED" -> Status.CANCELED;
            case "PENDING" -> Status.PENDING;
            default -> throw new NotFoundException("Неизвестный статус.");
        };
    }
}
