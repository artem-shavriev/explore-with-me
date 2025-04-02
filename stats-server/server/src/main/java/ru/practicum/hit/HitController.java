package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/hit")
@RequiredArgsConstructor
@Slf4j
@Validated
public class HitController {
    private final HitService hitServiceImpl;

    @PostMapping
    public void addHit(@RequestBody HitDto dto) {
        log.info("Запрос c ip {}добавлен.", dto.getIp());
        hitServiceImpl.addHit(dto);
    }
}
