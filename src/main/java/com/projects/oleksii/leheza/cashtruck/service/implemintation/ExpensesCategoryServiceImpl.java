package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.ExpensesCategory;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpenseCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpensesDto;
import com.projects.oleksii.leheza.cashtruck.repository.ExpensesCategoryRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ExpensesCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpensesCategoryServiceImpl implements ExpensesCategoryService {

    private final ExpensesCategoryRepository expensesCategoryRepository;

    @Override
    public void createCategory(ExpensesCategory expensesCategory) throws IllegalStateException {
        String categoryName = expensesCategory.getCategoryName();
        if (existByCategoryName(categoryName)) {
            throw new IllegalStateException("Category with " + categoryName + " name exists");
        }
        expensesCategoryRepository.save(expensesCategory);
    }

    @Override
    public void changeCategory(Long expensesCategoryId, ExpensesCategory expensesCategory) {
        String categoryName = expensesCategory.getCategoryName();
        if (existByCategoryName(categoryName)) {
            throw new IllegalStateException("Category with " + categoryName + " name exists");
        }
        expensesCategoryRepository.save(expensesCategory);
    }

    @Override
    public void deleteCategory(Long expensesCategoryId) throws IllegalStateException {
        if (!expensesCategoryRepository.findById(expensesCategoryId).isPresent()) {
            throw new IllegalStateException("Category doesn`t exist");
        }
        expensesCategoryRepository.deleteById(expensesCategoryId);
    }

    @Override
    public List<ExpensesCategory> findAll() {
        return expensesCategoryRepository.findAll();
    }


    private boolean existByCategoryName(String categoryName) {
        return expensesCategoryRepository.findByCategoryName(categoryName).isPresent();
    }
}
