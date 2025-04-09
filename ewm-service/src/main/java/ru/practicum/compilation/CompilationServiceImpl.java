package ru.practicum.compilation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.category.model.Category;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private  final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Override
    @Transactional
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable compilationPage = PageRequest.of(from, size);

        List<Compilation> compilationsList = (compilationRepository.findAll(compilationPage).getContent());

        log.info("Найдены подборки событий");
        return compilationMapper.mapToDto(compilationsList);
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
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto);

        compilation = compilationRepository.save(compilation);

        log.info("Добавлена подборка событий.");
        return compilationMapper.mapToDto(compilation);
    }

    @Override
    @Transactional
    public void deleteCompilation(@PathVariable Integer compId) {
        Compilation compilation = compilationRepository
                .findById(compId)
                .orElseThrow(() -> new NotFoundException("Удаляемая подборка не найдена по id"));

        log.info("Удалена подборка событий.");
        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Обновляемая подборка не найдена по id"));

        if (updateCompilation.hasEvents()) {
            compilation.setEvents(updateCompilation.getEvents());
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
