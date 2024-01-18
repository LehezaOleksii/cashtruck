package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.domain.ExpensesCategory;
import com.projects.oleksii.leheza.cashtruck.domain.IncomeCategory;
import com.projects.oleksii.leheza.cashtruck.repository.ExpensesCategoryRepository;
import com.projects.oleksii.leheza.cashtruck.repository.IncomeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration //TODO?
@RequiredArgsConstructor
public class MainEntitiesGenerator {

    private final ExpensesCategoryRepository expensesCategoryRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;

    public void generateMainEntities() {
        generateExpensesCategories();
        generateIncomeCategories();
    }

    private void generateExpensesCategories() {
        ExpensesCategory expensesCategorySport = new ExpensesCategory("Sport & Health");
        ExpensesCategory expensesCategoryRent = new ExpensesCategory("Rent & Mortgage");
        ExpensesCategory expensesCategoryUtilities = new ExpensesCategory("Utilities");
        ExpensesCategory expensesCategoryFood = new ExpensesCategory("Food");
        ExpensesCategory expensesCategoryDiningOut = new ExpensesCategory("Dining out");
        ExpensesCategory expensesCategoryEntertainment = new ExpensesCategory("Entertainment");
        ExpensesCategory expensesCategoryTransportation = new ExpensesCategory("Transportation & Car");
        ExpensesCategory expensesCategoryPersonalCare = new ExpensesCategory("Personal care");
        ExpensesCategory expensesCategoryDevices = new ExpensesCategory("Devices");
        ExpensesCategory expensesCategorySendMoney = new ExpensesCategory("Send money");
        ExpensesCategory expensesCategorySpendCash = new ExpensesCategory("Spend cash");
        expensesCategoryRepository.save(expensesCategorySport);
        expensesCategoryRepository.save(expensesCategoryRent);
        expensesCategoryRepository.save(expensesCategoryUtilities);
        expensesCategoryRepository.save(expensesCategoryFood);
        expensesCategoryRepository.save(expensesCategoryDiningOut);
        expensesCategoryRepository.save(expensesCategoryEntertainment);
        expensesCategoryRepository.save(expensesCategoryTransportation);
        expensesCategoryRepository.save(expensesCategoryPersonalCare);
        expensesCategoryRepository.save(expensesCategoryDevices);
        expensesCategoryRepository.save(expensesCategorySendMoney);
        expensesCategoryRepository.save(expensesCategorySpendCash);
    }

    private void generateIncomeCategories() {
        IncomeCategory incomeCategorySalary = new IncomeCategory("Job Salary");
        IncomeCategory incomeCategoryFreelance = new IncomeCategory("Freelance Income");
        IncomeCategory incomeCategoryRental = new IncomeCategory("Rental Income");
        IncomeCategory InvestmentsInvestments = new IncomeCategory("Investments");
        IncomeCategory incomeCategoryDonations = new IncomeCategory("Gifts & Donations");
        IncomeCategory incomeCategoryBusiness = new IncomeCategory("Business");
        IncomeCategory incomeCategoryReceiveMoney  = new IncomeCategory("Recive money");
        IncomeCategory incomeCategoryGetCash  = new IncomeCategory("Get cash");
        incomeCategoryRepository.save(incomeCategorySalary);
        incomeCategoryRepository.save(incomeCategoryFreelance);
        incomeCategoryRepository.save(incomeCategoryRental);
        incomeCategoryRepository.save(InvestmentsInvestments);
        incomeCategoryRepository.save(incomeCategoryDonations);
        incomeCategoryRepository.save(incomeCategoryBusiness);
        incomeCategoryRepository.save(incomeCategoryReceiveMoney);
        incomeCategoryRepository.save(incomeCategoryGetCash);
    }
}
