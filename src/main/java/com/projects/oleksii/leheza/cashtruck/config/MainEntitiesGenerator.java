package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.domain.ExpensesCategory;
import com.projects.oleksii.leheza.cashtruck.domain.IncomeCategory;
import com.projects.oleksii.leheza.cashtruck.repository.ExpensesCategoryRepository;
import com.projects.oleksii.leheza.cashtruck.repository.IncomeCategoryRepository;
import org.springframework.context.annotation.Configuration;

@Configuration //TODO?
public class MainEntitiesGenerator {

    public void generateMainEntities(ExpensesCategoryRepository expensesCategoryRepository, IncomeCategoryRepository incomeCategoryRepository) {
        generateExpensesCategories(expensesCategoryRepository);
        generateIncomeCategories(incomeCategoryRepository);
    }

    private void generateExpensesCategories(ExpensesCategoryRepository repository) {
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
        repository.save(expensesCategorySport);
        repository.save(expensesCategoryRent);
        repository.save(expensesCategoryUtilities);
        repository.save(expensesCategoryFood);
        repository.save(expensesCategoryDiningOut);
        repository.save(expensesCategoryEntertainment);
        repository.save(expensesCategoryTransportation);
        repository.save(expensesCategoryPersonalCare);
        repository.save(expensesCategoryDevices);
        repository.save(expensesCategorySendMoney);
        repository.save(expensesCategorySpendCash);
    }

    private void generateIncomeCategories(IncomeCategoryRepository repository) {
        IncomeCategory incomeCategorySalary = new IncomeCategory("Job Salary");
        IncomeCategory incomeCategoryFreelance = new IncomeCategory("Freelance Income");
        IncomeCategory incomeCategoryRental = new IncomeCategory("Rental Income");
        IncomeCategory InvestmentsInvestments = new IncomeCategory("Investments");
        IncomeCategory incomeCategoryDonations = new IncomeCategory("Gifts & Donations");
        IncomeCategory incomeCategoryBusiness = new IncomeCategory("Business");
        IncomeCategory incomeCategoryTaxes = new IncomeCategory("Taxes");
        IncomeCategory incomeCategoryReceiveMoney  = new IncomeCategory("Recive money");
        IncomeCategory incomeCategoryGetCash  = new IncomeCategory("Get cash");
        repository.save(incomeCategorySalary);
        repository.save(incomeCategoryFreelance);
        repository.save(incomeCategoryRental);
        repository.save(InvestmentsInvestments);
        repository.save(incomeCategoryDonations);
        repository.save(incomeCategoryBusiness);
        repository.save(incomeCategoryTaxes);
        repository.save(incomeCategoryReceiveMoney);
        repository.save(incomeCategoryGetCash);
    }
}
