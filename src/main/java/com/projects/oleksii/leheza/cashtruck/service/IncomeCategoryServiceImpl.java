package com.projects.oleksii.leheza.cashtruck.service;

import com.projects.oleksii.leheza.cashtruck.domain.IncomeCategory;
import com.projects.oleksii.leheza.cashtruck.repository.IncomeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeCategoryServiceImpl implements IncomeCategoryService {

    private final IncomeCategoryRepository incomeCategoryRepository;

    @Override
    public void createCategory(IncomeCategory incomeCategory) throws IllegalStateException {
        String categoryName = incomeCategory.getCategoryName();
        if (existByCategoryName(categoryName)) {
            throw new IllegalStateException("Category with " + categoryName + " name exists");
        }
        incomeCategoryRepository.save(incomeCategory);
    }

    @Override
    public void changeCategory(Long incomeCategoryId, IncomeCategory incomeCategory) {
        String categoryName = incomeCategory.getCategoryName();
        if (existByCategoryName(categoryName)) {
            throw new IllegalStateException("Category with " + categoryName + " name exists");
        }
        incomeCategoryRepository.save(incomeCategory);
    }

    @Override
    public void deleteCategory(Long incomeCategoryId) throws IllegalStateException {
        if (!incomeCategoryRepository.findById(incomeCategoryId).isPresent()) {
            throw new IllegalStateException("Category doesn`t exist");
        }
        incomeCategoryRepository.deleteById(incomeCategoryId);
    }

    private boolean existByCategoryName(String categoryName) {
        return incomeCategoryRepository.findByCategoryName(categoryName).isPresent();
    }
}
