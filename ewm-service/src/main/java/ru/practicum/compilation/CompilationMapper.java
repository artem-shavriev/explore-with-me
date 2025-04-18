package ru.practicum.compilation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;

@Component
@AllArgsConstructor
public class CompilationMapper {
    private final EventService eventService;

    public CompilationDto mapToDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());

        List<EventShortDto> eventShortDtoList = eventService.setViewsForShortDto(compilation.getEvents());
        compilationDto.setEvents(eventShortDtoList);

        return compilationDto;
    }

    public Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto, List<Event> events) {

        return Compilation.builder().events(events)
                .pinned(newCompilationDto.getPinned()).title(newCompilationDto.getTitle()).build();
    }
}
