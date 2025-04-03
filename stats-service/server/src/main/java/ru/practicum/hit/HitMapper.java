package ru.practicum.hit;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.EndpointHit;
import ru.practicum.exception.NotFoundException;
import ru.practicum.hit.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@NoArgsConstructor
public class HitMapper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Hit mapToHit(EndpointHit dto) {
        if (dto == null) {
            throw new NotFoundException("HitDto не может быть равен null");
        }
        Hit hit = new Hit();

        hit.setIp(dto.getIp());
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());

        LocalDateTime dateTime = LocalDateTime.parse(dto.getTimestamp(), formatter);

        hit.setTimestamp(dateTime);

        return hit;
    }
}
