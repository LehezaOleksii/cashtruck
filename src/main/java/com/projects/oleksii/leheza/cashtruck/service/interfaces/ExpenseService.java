package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Expense;
import com.projects.oleksii.leheza.cashtruck.domain.Income;

import java.util.List;

public interface ExpenseService {

    List<Expense> findAll();

    void save(Expense expense);
}
