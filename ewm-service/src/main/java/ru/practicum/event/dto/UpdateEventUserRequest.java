package ru.practicum.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.Location;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    private CategoryDto category;
    @Size(min = 20, max = 7000)
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    @Min(0)
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(min = 3, max = 120)
    private String title;

    public boolean hasAnnotation() {
        return annotation != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasCategory() {
        return category != null;
    }

    public boolean hasEventDate() {
        return eventDate != null;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasPaid() {
        return paid != null;
    }

    public boolean hasParticipantLimit() {
        return participantLimit != null;
    }

    public boolean hasRequestModeration() {
        return requestModeration != null;
    }

    public boolean hasStateAction() {
        return stateAction != null;
    }

    public boolean hasTitle() {
        return title != null;
    }
}
