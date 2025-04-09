package ru.practicum.event.model;

import lombok.ToString;

@ToString
public enum State {
    PENDING,
    PUBLISHED,
    CANCELED
}
