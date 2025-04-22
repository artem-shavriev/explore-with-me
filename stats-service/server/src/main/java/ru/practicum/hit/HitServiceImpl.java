package ru.practicum.hit;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.HitRepository;
import ru.practicum.hit.model.Hit;

@Service
@RequiredArgsConstructor
@Slf4j
public class HitServiceImpl implements HitService {
    private final HitMapper hitMapper;
    private final HitRepository repository;

    @Override
    @Transactional
    public void addHit(EndpointHit dto) {
        Hit hit = hitMapper.mapToHit(dto);
        repository.save(hit);
    }
}
