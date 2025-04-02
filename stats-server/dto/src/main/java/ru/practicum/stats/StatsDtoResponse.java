package ru.practicum.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatsDtoResponse {
    private String app;
    private String uri;
    private Integer hits;
}
