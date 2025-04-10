package ru.practicum.event.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.State;
import ru.practicum.location.Location;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UpdateEventAdminRequest {
    @Min(20)
    @Max(2000)
    private String annotation;
    private CategoryDto category;
    @Min(20)
    @Max(7000)
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Min(3)
    @Max(120)
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
        return title!= null;
    }
}
