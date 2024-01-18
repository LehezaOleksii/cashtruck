package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Income;
import com.projects.oleksii.leheza.cashtruck.dto.DtoConvertor;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpenseCategoryDto;
import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpensesDto;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;
import com.projects.oleksii.leheza.cashtruck.repository.IncomeRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final DtoConvertor dtoConvertor;
    private final ClientRepository clientRepository;

    @Override
    public List<Income> findAll() {
        return incomeRepository.findAll();
    }

    @Override
    public void save(Income income) {
        incomeRepository.save(income);
    }

    @Override
    public List<IncomeExpenseCategoryDto> findClientIncomesCategories(Long clientId) {
        return findIncomesByClientId(clientId).stream()
                .map(incomeExpensesDto -> dtoConvertor.incomeCategoryToDto(clientRepository.getReferenceById(clientId), incomeExpensesDto.getCategory()))
                .distinct()
                .toList();
    }

    @Override
    public List<IncomeExpensesDto> findIncomesByClientId(Long clientId) {
        return clientRepository.findById(clientId).stream()
                .flatMap(client -> client.getIncomes().stream())
                .map(dtoConvertor::incomeToDto)
                .toList();
    }
}
