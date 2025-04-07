package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NewCompilationDto {
    private List<Integer> events;
    private Boolean pinned = false;
    @NotBlank
    @Min(1)
    @Max(50)
    private String title;
}
