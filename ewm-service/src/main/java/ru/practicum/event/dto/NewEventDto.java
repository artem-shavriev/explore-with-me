package ru.practicum.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.location.Location;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NewEventDto {
    @NotBlank
    @Min(20)
    @Max(2000)
    private String annotation;
    @NotNull
    private CategoryDto category;
    @NotBlank
    @Min(20)
    @Max(7000)
    private String description;
    @NotBlank
    private String eventDate;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotNull
    @Min(3)
    @Max(120)
    private String title;
}
