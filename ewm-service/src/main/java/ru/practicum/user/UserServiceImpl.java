package ru.practicum.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден."));

        log.info("Пользователь получен по id");
        return userMapper.mapToDto(user);
    }

    @Override
    @Transactional
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Запрос составлен некорректно, from и size должны быть положительными.");
        }
        Pageable usersPage = PageRequest.of(from, size);

        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = (userRepository.findAll(usersPage).getContent());
        } else {
            users = (userRepository.findUsersByIdsList(usersPage, ids));
        }
        log.info("Пользователи получены.");
        return userMapper.mapToDto(users);
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        if (!userRepository.findAllByEmail(newUserRequest.getEmail()).isEmpty()) {
            throw new ConflictException("Пользователь с таким имейлом уже существует.");
        }
        User user = userMapper.newUserRequestToUser(newUserRequest);

        user = userRepository.save(user);

        log.info("Пользователь добавлен.");
        return userMapper.mapToDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Удаляемы пользователь не найден."));

        log.info("Пользователь удален.");
        userRepository.delete(user);
    }
}
