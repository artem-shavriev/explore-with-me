package ru.practicum.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EventFullDtoWithComments {
    private Integer id;
    @NotBlank
    private String annotation;
    @NotNull
    private CategoryDto category;
    private Integer confirmedRequests;
    private String createdOn;
    private String description;
    @NotBlank
    private String eventDate;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private Location location;
    @NotNull
    private Boolean paid;
    private Integer participantLimit = 0;
    private String publishedOn;
    private Boolean requestModeration = true;
    private String state;
    @NotNull
    private String title;
    private Long views;
    private List<CommentDto> comments;
}

