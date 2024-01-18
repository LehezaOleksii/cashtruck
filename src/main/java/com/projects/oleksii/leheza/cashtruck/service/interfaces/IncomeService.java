package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Income;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpenseCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpensesDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeService {

//    void saveIncome(Income income, Long clientId);

    List<Income> findAll();

    void save(Income income);

    List<IncomeExpenseCategoryDto> findClientIncomesCategories(Long clientId);

    List<IncomeExpensesDto> findIncomesByClientId(Long clientId);
}
