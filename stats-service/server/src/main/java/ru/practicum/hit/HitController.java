package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EndpointHit;

@RestController
@RequestMapping("/hit")
@RequiredArgsConstructor
@Slf4j
public class HitController {
    private final HitService hitServiceImpl;

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody EndpointHit dto) {
        log.info("Запрос c ip {} и uri {} добавлен.", dto.getIp(),dto.getUri());
        hitServiceImpl.addHit(dto);
    }
}
