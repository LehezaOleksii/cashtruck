package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.PageDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.CategoryDto;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.CategoryRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private static final TransactionType transactionTypeIncome = TransactionType.INCOME;
    private static final TransactionType transactionTypeExpense = TransactionType.EXPENSE;

    private final CategoryRepository categoryRepository;
    private final DtoMapper dtoMapper;

//    @Override TODO
//    public List<Category> findAll() {
//        return categoryRepository.findAll();
//    }
//
//    @Override
//    public Category save(Category category) {
//        return categoryRepository.save(category);
//    }

    @Override
    public Category save(CreateCategoryDto category) {
        log.info("create new category with name: {}", category.getCategoryName());
        return categoryRepository.save(dtoMapper.categoryDtoToCategory(category));
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

    @Override
    public List<CategoryDto> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(dtoMapper::categoryToDto)
                .toList();
    }

    @Override
    public PageDto<CategoryDto> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<CategoryDto> categories = categoryRepository.findAll(pageable).stream()
                .map(dtoMapper::categoryToDto).toList();
        PageDto<CategoryDto> categoriesPageDto = PageDto.<CategoryDto>builder()
                .data(categories)
                .page(pageNumber)
                .size(pageSize)
                .totalSize(categories.size())
                .build();
        categoriesPageDto = categoriesPageDto.toBuilder()
                .totalPage((int) Math.ceil((double) categories.size() / pageSize))
                .build();
        return categoriesPageDto;
    }

    @Override
    public CreateCategoryDto findById(Long categoryId) {
        return dtoMapper.categoryToCreateDto(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category does not found")));
    }
}
