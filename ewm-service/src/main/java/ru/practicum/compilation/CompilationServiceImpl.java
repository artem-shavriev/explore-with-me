package ru.practicum.compilation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.EventRepository;
import ru.practicum.event.EventService;
import ru.practicum.event.model.Event;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable compilationPage = PageRequest.of(from, size);

        Page<Compilation> compilationsPage = (compilationRepository.findAllByPinned(pinned, compilationPage));

        log.info("Найдены подборки событий");
        return compilationsPage.stream().map(compilationMapper::mapToDto).toList();
    }

    @Override
    @Transactional
    public CompilationDto getCompilationById(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена по id"));

        log.info("Найдена подборка событий по id");
        return compilationMapper.mapToDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findByIdList(newCompilationDto.getEvents());

        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto, events);

        compilation = compilationRepository.save(compilation);

        log.info("Добавлена подборка событий.");
        return compilationMapper.mapToDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(Integer compId) {
        compilationRepository
                .findById(compId)
                .orElseThrow(() -> new NotFoundException("Удаляемая подборка не найдена по id"));

        compilationRepository.deleteById(compId);
        log.info("Удалена подборка событий c id {}.", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Обновляемая подборка не найдена по id"));


        List<Event> events = eventRepository.findByIdList(updateCompilation.getEvents());
        if (updateCompilation.hasEvents()) {
            compilation.setEvents(events);
        }

        if (updateCompilation.hasTitle()) {
            compilation.setTitle(updateCompilation.getTitle());
        }

        if (updateCompilation.hasPinned()) {
            compilation.setPinned(updateCompilation.getPinned());
        }

        compilation = compilationRepository.save(compilation);

        log.info("Обновлена подборка событий");
        return compilationMapper.mapToDto(compilation);
    }
}
