package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RequestParamsDto {
    String text;
    List<Integer> categories;
    Boolean paid;
    String rangeStart;
    String rangeEnd;
    Boolean onlyAvailable;
    String sort;
    Integer from;
    Integer size;
    String uri;
    String ip;
}
