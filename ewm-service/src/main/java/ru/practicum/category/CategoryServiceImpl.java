package ru.practicum.category;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable categoryPage = PageRequest.of(from, size);
        List<Category> allCategories = (categoryRepository.findAll(categoryPage)).getContent();
        log.info("Категории найдены");
        return categoryMapper.mapToDto(allCategories);
    }

    @Override
    @Transactional
    public CategoryDto getByIdCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория не найдена по id"));
        log.info("Категория найдена");
        return categoryMapper.mapToDto(category);
    }

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (!categoryRepository.findAllByName(newCategoryDto.getName()).isEmpty()) {
            log.error("Запрос составлен некорректно.");
            throw new ConflictException("Категория с таким именем уже существует.");
        }

        Category category = categoryMapper.newCategoryToCategory(newCategoryDto);

        category = categoryRepository.save(category);

        log.info("Категория id: {} добавлена", category.getId());
        return categoryMapper.mapToDto(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория не найдена или недоступна."));

        if (!eventRepository.findAllByCategoryOrderByEventDateDesc(id).isEmpty()) {
            log.error("Существуют события, связанные с категорией {}", category.getName());
            throw  new ConflictException("Существуют события, связанные с категорией.");
        }

        log.info("Категория удалена");
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Integer id, NewCategoryDto newCategoryDto) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория не найдена по id"));

        if (!categoryRepository.findAllByNameWithoutCurrent(newCategoryDto.getName(), id).isEmpty()) {
            throw new ConflictException("Имя категории уже занято.");
        }

        category.setName(newCategoryDto.getName());
        category = categoryRepository.save(category);

        log.info("Данные категории изменены");
        return categoryMapper.mapToDto(category);
    }
}
