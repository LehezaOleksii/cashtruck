package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.CategoryRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final TransactionType transactionTypeIncome = TransactionType.INCOME;
    private static final TransactionType transactionTypeExpense = TransactionType.EXPENSE;

    private final CategoryRepository categoryRepository;
    private final DtoMapper dtoMapper;


    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findAllIncomeCategories() {
        return categoryRepository.findByTransactionType(transactionTypeIncome);
    }

    @Override
    public List<Category> findAllExpensesCategories() {
        return categoryRepository.findByTransactionType(transactionTypeExpense);
    }

    @Override
    public CategoryDto findByName(String categoryName) {
        return dtoMapper.categoryToDto(categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new ResourceNotFoundException("Category with name" + categoryName + " was not found")));
    }
}
