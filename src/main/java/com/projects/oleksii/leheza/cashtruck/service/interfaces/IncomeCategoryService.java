package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.IncomeCategory;

import java.util.List;

public interface IncomeCategoryService {


    public void createCategory(IncomeCategory incomeCategory);

    public void changeCategory(Long incomeCategoryId, IncomeCategory incomeCategory);

    public void deleteCategory(Long incomeCategoryId);

    public List<IncomeCategory> findAll();
}
