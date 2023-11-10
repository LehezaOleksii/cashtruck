package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Expense;
import com.projects.oleksii.leheza.cashtruck.repository.ExpensesRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

private final ExpensesRepository expensesRepository;

    @Override
    public List<Expense> findAll() {
        return expensesRepository.findAll();
    }

    @Override
    public void save(Expense expense) {
        expensesRepository.save(expense);
    }
}
