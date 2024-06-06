package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryDto;

import java.util.List;

public interface CategoryService {


    List<Category> findAll();

    Category save(Category category);

    List<Category> findAllIncomeCategories();

    List<Category> findAllExpensesCategories();

    CategoryDto findByName(String categoryName);
}
