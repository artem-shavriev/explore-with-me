package ru.practicum.category;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

@Component
@AllArgsConstructor
public class CategoryMapper {
    public CategoryDto mapToDto(Category category) {
        return CategoryDto.builder().id(category.getId())
                .name(category.getName()).build();
    }

    public List<CategoryDto> mapToDto(List<Category> categories) {
        return categories.stream().map(this :: mapToDto).toList();
    }

    public Category newCategoryToCategory(NewCategoryDto newCategoryDto) {
        return Category.builder().name(newCategoryDto.getName()).build();
    }
}
