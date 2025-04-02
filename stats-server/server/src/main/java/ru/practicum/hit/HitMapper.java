package ru.practicum.hit;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NotFoundException;
import ru.practicum.hit.model.Hit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@NoArgsConstructor
public class HitMapper {
    public HitDto mapToHitDto(Hit hit) {
        if (hit == null) {
            throw new NotFoundException("Hit не может быть равен null");
        }
        HitDto hitDto = new HitDto();

        hitDto.setIp(hit.getIp());
        hitDto.setApp(hit.getApp());
        hitDto.setUri(hit.getUri());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String dateTimeString = hit.getTimestamp().format(dateTimeFormatter);

        hitDto.setTimestamp(dateTimeString);

        return hitDto;
    }

    public Hit mapToHit(HitDto dto) {
        if (dto== null) {
            throw new NotFoundException("HitDto не может быть равен null");
        }
        Hit hit = new Hit();

        hit.setIp(dto.getIp());
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());

        LocalDateTime dateTime = LocalDateTime.parse(dto.getTimestamp());
        hit.setTimestamp(dateTime);

        return hit;
    }
}
