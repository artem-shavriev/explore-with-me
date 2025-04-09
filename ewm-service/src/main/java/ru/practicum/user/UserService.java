package ru.practicum.user;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.participation.dto.ParticipationRequestDto;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    UserDto getUserById(Integer id);

    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);

    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUser(Integer userId);

    List<ParticipationRequestDto> getUserRequests(Integer userId);

    ParticipationRequestDto addParticipation(Integer userId, Integer eventId);

    ParticipationRequestDto cancelRequest(Integer userId, Integer requestId);
}
