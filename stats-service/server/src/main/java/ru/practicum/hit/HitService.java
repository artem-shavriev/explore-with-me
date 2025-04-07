package ru.practicum.hit;

import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;

@Service
public interface HitService {
    void addHit(EndpointHit dto);
}
