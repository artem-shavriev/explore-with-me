package ru.practicum.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Location;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotNull
    private Integer category;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    @NotBlank
    private String eventDate;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid = false;
    @Min(0)
    private Integer participantLimit = 0;
    private Boolean requestModeration = true;
    @NotNull
    @Size(min = 3, max = 120)
    private String title;
}
