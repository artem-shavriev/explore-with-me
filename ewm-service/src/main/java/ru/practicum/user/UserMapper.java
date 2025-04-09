package ru.practicum.user;

import org.springframework.stereotype.Component;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.util.List;

@Component
public class UserMapper {
    public UserDto mapToDto(User user) {
        return UserDto.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).build();
    }

    public List<UserDto> mapToDto(List<User> users) {
        return users.stream().map(this::mapToDto).toList();
    }

    public UserShortDto dtoToShortDto(UserDto userDto) {
        return UserShortDto.builder().id(userDto.getId()).name(userDto.getName()).build();
    }

    public User newUserRequestToUser(NewUserRequest newUserRequest) {
        return User.builder().name(newUserRequest.getName())
                .email(newUserRequest.getEmail()).build();
    }
}
