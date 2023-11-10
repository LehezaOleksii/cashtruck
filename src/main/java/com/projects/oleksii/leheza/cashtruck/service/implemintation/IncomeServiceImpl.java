package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Income;
import com.projects.oleksii.leheza.cashtruck.repository.IncomeRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    @Override
    public List<Income> findAll() {
        return incomeRepository.findAll();
    }

    @Override
    public void save(Income income) {
        incomeRepository.save(income);
    }
}
