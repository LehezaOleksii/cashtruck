//package com.projects.oleksii.leheza.cashtruck.service.implemintation;
//
//import com.projects.oleksii.leheza.cashtruck.domain.Expense;
//import com.projects.oleksii.leheza.cashtruck.dto.DtoConvertor;
//import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpenseCategoryDto;
//import com.projects.oleksii.leheza.cashtruck.dto.view.IncomeExpensesDto;
//import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;
//import com.projects.oleksii.leheza.cashtruck.repository.ExpensesRepository;
//import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
//import com.projects.oleksii.leheza.cashtruck.service.interfaces.ExpenseService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class ExpenseServiceImpl implements ExpenseService {
//
//    private final ExpensesRepository expensesRepository;
//    private final ClientRepository clientRepository;
//    private final DtoConvertor dtoConvertor;
//
//    @Override
//    public List<Expense> findAll() {
//        return expensesRepository.findAll();
//    }
//
//    @Override
//    public void save(Expense expense) {
//        expensesRepository.save(expense);
//    }
//
//    @Override
//    public List<IncomeExpenseCategoryDto> findClientExpensesCategories(Long clientId) {
//        return findExpensesByClientId(clientId).stream()
//                .map(incomeExpensesDto -> dtoConvertor.expenseCategoryToDto(clientRepository.getReferenceById(clientId), incomeExpensesDto.getCategory()))
//                .distinct()
//                .toList();
//    }
//
//    @Override
//    public List<IncomeExpensesDto> findExpensesByClientId(Long clientId) {
//        return clientRepository.findById(clientId).stream()
//                .flatMap(client -> client.getExpenses().stream())
//                .map(dtoConvertor::expenseToDto)
//                .toList();
//    }
//}
