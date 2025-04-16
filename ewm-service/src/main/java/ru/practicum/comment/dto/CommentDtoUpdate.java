package ru.practicum.comment.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentDtoUpdate {
    @Size(min = 5, max = 2000)
    private String text;
    private String status;

    public boolean hasText() {
        return text != null;
    }
    public boolean hasStatus() {
        return status != null;}
}

