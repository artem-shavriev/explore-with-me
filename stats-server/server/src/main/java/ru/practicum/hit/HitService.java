package ru.practicum.hit;

import org.springframework.stereotype.Service;

@Service
public interface HitService {
    void addHit(HitDto dto);

}
