package ru.practicum.comment.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentDtoUpdate {
    private Integer id;
    @Size(min = 5, max = 2000)
    private String text;
    private String status;
    private Integer eventId;
    private Integer authorId;
    private LocalDateTime created;

    public boolean hasText() {
        return text != null;
    }
}

