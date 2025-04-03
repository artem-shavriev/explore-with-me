package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EndpointHit;

@RestController
@RequestMapping("/hit")
@RequiredArgsConstructor
@Slf4j
public class HitController {
    private final HitService hitServiceImpl;

   @PostMapping
    public void addHit(@RequestBody EndpointHit dto) {
        log.info("Запрос c ip {}добавлен.", dto.getIp());
        hitServiceImpl.addHit(dto);
    }
}
