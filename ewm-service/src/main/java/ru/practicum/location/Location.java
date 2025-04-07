package ru.practicum.location;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private double lat;
    private double lon;
}
