package ru.practicum.user;

import org.springframework.stereotype.Service;
import ru.practicum.user.dto.UserDto;

@Service
public interface UserService {
    UserDto getUserById(Integer id);
}
