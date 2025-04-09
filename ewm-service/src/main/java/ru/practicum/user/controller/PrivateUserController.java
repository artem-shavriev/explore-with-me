package ru.practicum.user.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.user.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateUserController {
    private final UserService userService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Integer userId) {
        return userService. getUserRequests(userId);
    }

    @PostMapping
    public ParticipationRequestDto addParticipation(@PathVariable Integer userId,
                                                    @RequestParam Integer eventId) {
       return userService.addParticipation(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Integer userId,
                                                 @PathVariable Integer requestId) {
        return userService.cancelRequest(userId, requestId);
    }
}
