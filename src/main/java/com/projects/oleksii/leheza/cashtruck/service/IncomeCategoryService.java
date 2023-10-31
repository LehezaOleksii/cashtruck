package com.projects.oleksii.leheza.cashtruck.service;

import com.projects.oleksii.leheza.cashtruck.domain.IncomeCategory;

public interface IncomeCategoryService {

    public void createCategory(IncomeCategory incomeCategory);

    public void changeCategory(Long incomeCategoryId, IncomeCategory incomeCategory);

    public void deleteCategory(Long incomeCategoryId);
}
