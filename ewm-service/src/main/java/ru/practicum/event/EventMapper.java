package ru.practicum.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.CategoryService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserShortDto;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class EventMapper {
    private final CategoryService categoryService;
    private final UserService userService;
    private final UserMapper userMapper;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventShortDto eventToShortDto(Event event) {
        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());

        CategoryDto category = categoryService.getByIdCategory(event.getCategory());
        eventShortDto.setCategory(category);


        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());

        String eventDated = event.getEventDate().format(formatter);
        eventShortDto.setEventDate(eventDated);

        UserShortDto user = userMapper.dtoToShortDto(userService.getUserById(event.getInitiator()));
        eventShortDto.setInitiator(user);

        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(event.getViews());

        return eventShortDto;
    }

    public List<EventShortDto> eventToShortDto(List<Event> events) {
        return events.stream().map(this::eventToShortDto).toList();
    }
}
