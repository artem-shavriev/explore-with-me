package ru.practicum.category;

import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

@Service
public interface CategoryService {
    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getByIdCategory(Integer id);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Integer id);

    CategoryDto updateCategory(Integer id, NewCategoryDto newCategoryDto);
}
