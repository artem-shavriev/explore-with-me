package ru.practicum.category.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.ReadOnlyProperty;

@Data
@Builder
public class CategoryDto {
    @ReadOnlyProperty
    private Integer id;
    @NotBlank
    @Max(50)
    @Min(1)
    private String name;
}
