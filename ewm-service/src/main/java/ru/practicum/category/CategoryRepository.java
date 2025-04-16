package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByName(String name);

    List<Category> findAllByNameAndIdNot(String name, Integer id);

    Category findById();
}
