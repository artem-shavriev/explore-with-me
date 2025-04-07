package ru.practicum.category.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewCategoryDto {
    @NotBlank
    @Max(50)
    @Min(1)
    private String name;
}
