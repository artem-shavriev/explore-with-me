package ru.practicum.user;

import org.springframework.stereotype.Component;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

@Component
public class UserMapper {
    public UserDto mapToDto(User user) {
        return UserDto.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).build();
    }

    public UserShortDto dtoToShortDto(UserDto userDto) {
        return UserShortDto.builder().id(userDto.getId()).name(userDto.getName()).build();
    }
}
