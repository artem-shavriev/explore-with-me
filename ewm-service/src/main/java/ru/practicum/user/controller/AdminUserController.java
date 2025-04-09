package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam List<Integer> ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @PostMapping
    public UserDto addUser(@RequestBody NewUserRequest newUserRequest) {
        return userService.addUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
    }
}
