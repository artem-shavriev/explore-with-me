package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByName(String name);

    @Query("SELECT c FROM Category c " +
            "WHERE c.name = ?1 AND c.id != ?2")
    List<Category> findAllByNameWithoutCurrent(String name, Integer id);
}
