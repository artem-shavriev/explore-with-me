package ru.practicum.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.location.Location;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @Column(name = "category_id", nullable = false)
    private Integer category;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "description")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "initiator", nullable = false)
    private Integer initiator;

    @Column(name = "event_location", nullable = false)
    private List<Double> location;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "published_on")
    private LocalDateTime published_on;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "state")
    private String state;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "views")
    private Integer views;
}
