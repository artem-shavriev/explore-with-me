package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CompilationDto {
    @NotNull
    private Integer id;
    private List<EventShortDto> events;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;
}
