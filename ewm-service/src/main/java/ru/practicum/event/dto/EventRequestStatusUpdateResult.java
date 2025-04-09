package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.participation.dto.ParticipationRequestDto;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EventRequestStatusUpdateResult {
    private ParticipationRequestDto confirmedRequests;
    private ParticipationRequestDto rejectedRequests;

}
