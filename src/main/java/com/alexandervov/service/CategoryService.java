package com.alexandervov.service;

import com.alexandervov.entity.Category;
import com.alexandervov.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Map<Integer, Category> categoriesMap = new HashMap<>();

    /**
     * Method for getting category instance by id.
     * @param id numeric identifier
     * @return category instance
     */
    public Category getCategory(int id) {
        if (categoriesMap.isEmpty()) {
            initCategoriesMap();
        }

        return categoriesMap.get(id);
    }

    /**
     * Method for getting category instance by name.
     * @param name of category
     * @return category instance
     */
    public Category getCategory(String name) throws IllegalArgumentException {
        if (categoriesMap.isEmpty()) {
            initCategoriesMap();
        }

        return categoriesMap.values().stream()
            .filter(c -> c.getName().equals(name))
            .findAny().orElseThrow(() -> new IllegalArgumentException("Wrong category name: " + name));
    }

    /**
     * Method for getting all categories.
     * @return all categories
     */
    public Collection<Category> getAllCategories() {
        return categoriesMap.values();
    }

    private void initCategoriesMap() {
        categoryRepository.findAll().forEach(c -> categoriesMap.put(c.getId(), c));
    }
}
