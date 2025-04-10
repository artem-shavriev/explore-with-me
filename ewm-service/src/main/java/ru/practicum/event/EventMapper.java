package ru.practicum.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.CategoryService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.location.Location;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class EventMapper {
    private final CategoryService categoryService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    public EventFullDto eventToFullDto(Event event) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());

        CategoryDto category = categoryService.getByIdCategory(event.getCategory());
        eventFullDto.setCategory(category);

        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn().format(formatter));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate().format(formatter));

        UserShortDto user = userMapper.dtoToShortDto(userService.getUserById(event.getInitiator()));
        eventFullDto.setInitiator(user);

        Location location = Location.builder().lat(event.getLocation().get(0)).lon(event.getLocation().get(1)).build();
        eventFullDto.setLocation(location);

        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setPublishedOn(event.getPublishedOn().format(formatter));
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(event.getViews());

        return eventFullDto;
    }

    public Event newEventDtoToEvent(NewEventDto newEventDto) {
        Event event = new Event();
        LocalDateTime newEventDate = LocalDateTime.parse(newEventDto.getEventDate(), formatter);

        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(newEventDto.getCategory().getId());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDate);

        List<Double> location = new ArrayList<>();
        Double lat = newEventDto.getLocation().getLat();
        Double lon = newEventDto.getLocation().getLon();
        location.add(lat);
        location.add(lon);
        event.setLocation(location);

        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());

        return event;
    }
}
