package ru.practicum.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserMapper;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class EventMapper {
    private final UserMapper userMapper;
    private  final CategoryMapper categoryMapper;
    private  final CategoryRepository categoryRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventShortDto eventToShortDto(Event event, Long views) {
        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());

        CategoryDto category = categoryMapper.mapToDto(event.getCategory());
        eventShortDto.setCategory(category);

        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());

        String eventDated = event.getEventDate().format(formatter);
        eventShortDto.setEventDate(eventDated);

        UserShortDto user = userMapper.dtoToShortDto(userMapper.mapToDto(event.getInitiator()));
        eventShortDto.setInitiator(user);

        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setViews(views);

        return eventShortDto;
    }

    public EventFullDto eventToFullDto(Event event, Long views) {
        EventFullDto eventFullDto = new EventFullDto();

        eventFullDto.setId(event.getId());
        eventFullDto.setAnnotation(event.getAnnotation());

        CategoryDto category = categoryMapper.mapToDto(event.getCategory());
        eventFullDto.setCategory(category);

        eventFullDto.setConfirmedRequests(event.getConfirmedRequests());
        eventFullDto.setCreatedOn(event.getCreatedOn().format(formatter));
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate().format(formatter));

        UserShortDto user = userMapper.dtoToShortDto(userMapper.mapToDto(event.getInitiator()));
        eventFullDto.setInitiator(user);

        Location location = Location.builder().lat(event.getLocation().get(0)).lon(event.getLocation().get(1)).build();
        eventFullDto.setLocation(location);

        eventFullDto.setPaid(event.getPaid());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());

        if (event.getPublishedOn() != null) {
            eventFullDto.setPublishedOn(event.getPublishedOn().format(formatter));
        }

        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setState(event.getState());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setViews(views);

        return eventFullDto;
    }

    public Event newEventDtoToEvent(NewEventDto newEventDto) {
        Event event = new Event();
        LocalDateTime newEventDate = LocalDateTime.parse(newEventDto.getEventDate(), formatter);

        event.setAnnotation(newEventDto.getAnnotation());

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Такой категории нет."));
        event.setCategory(category);

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
