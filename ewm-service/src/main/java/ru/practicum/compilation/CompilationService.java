package ru.practicum.compilation;

import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

@Service
public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);
}
