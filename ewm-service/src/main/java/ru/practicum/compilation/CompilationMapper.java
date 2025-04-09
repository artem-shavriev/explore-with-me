package ru.practicum.compilation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;

@Component
@AllArgsConstructor
public class CompilationMapper {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public CompilationDto mapToDto(Compilation compilation) {
        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());

        List<Event> eventList = eventRepository.findByIdList(compilation.getEvents());
        List<EventShortDto> eventShortDtoList = eventMapper.eventToShortDto(eventList);
        compilationDto.setEvents(eventShortDtoList);

        return compilationDto;
    }

    public List<CompilationDto> mapToDto(List<Compilation> compilationsList) {
        return compilationsList.stream().map(this::mapToDto).toList();
    }

    public Compilation newCompilationDtoToCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder().events(newCompilationDto.getEvents())
                .pinned(newCompilationDto.getPinned()).title(newCompilationDto.getTitle()).build();
    }
}
