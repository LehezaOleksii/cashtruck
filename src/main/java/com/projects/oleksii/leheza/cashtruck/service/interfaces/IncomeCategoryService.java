package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.IncomeCategory;

import java.util.List;

public interface IncomeCategoryService {


    void createCategory(IncomeCategory incomeCategory);

    void changeCategory(Long incomeCategoryId, IncomeCategory incomeCategory);

    void deleteCategory(Long incomeCategoryId);

    List<IncomeCategory> findAll();
}
