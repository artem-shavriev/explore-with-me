package ru.practicum.hit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
