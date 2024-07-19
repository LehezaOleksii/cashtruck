package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.domain.Subscription;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.repository.CategoryRepository;
import com.projects.oleksii.leheza.cashtruck.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration //TODO?
@RequiredArgsConstructor
public class MainEntitiesGenerator {

    private final CategoryRepository categoryRepository;
    private final SubscriptionRepository subscriptionRepository;

    private final TransactionType transactionTypeExpense = TransactionType.EXPENSE;
    private final TransactionType transactionTypeIncome = TransactionType.INCOME;


    public void generateMainEntities() {
        generateExpensesCategories();
        generateIncomeCategories();
        generatePlans();
    }

    private void generateExpensesCategories() {
        Category expensesCategorySport = new Category(transactionTypeExpense, "Sport & Health");
        Category expensesCategoryRent = new Category(transactionTypeExpense, "Rent & Mortgage");
        Category expensesCategoryUtilities = new Category(transactionTypeExpense, "Utilities");
        Category expensesCategoryFood = new Category(transactionTypeExpense, "Food");
        Category expensesCategoryDiningOut = new Category(transactionTypeExpense, "Dining out");
        Category expensesCategoryEntertainment = new Category(transactionTypeExpense, "Entertainment");
        Category expensesCategoryTransportation = new Category(transactionTypeExpense, "Transportation & Car");
        Category expensesCategoryPersonalCare = new Category(transactionTypeExpense, "Personal care");
        Category expensesCategoryDevices = new Category(transactionTypeExpense, "Devices");
        Category expensesCategorySendMoney = new Category(transactionTypeExpense, "Send money");
        Category expensesCategorySpendCash = new Category(transactionTypeExpense, "Spend cash");
        categoryRepository.save(expensesCategorySport);
        categoryRepository.save(expensesCategoryRent);
        categoryRepository.save(expensesCategoryUtilities);
        categoryRepository.save(expensesCategoryFood);
        categoryRepository.save(expensesCategoryDiningOut);
        categoryRepository.save(expensesCategoryEntertainment);
        categoryRepository.save(expensesCategoryTransportation);
        categoryRepository.save(expensesCategoryPersonalCare);
        categoryRepository.save(expensesCategoryDevices);
        categoryRepository.save(expensesCategorySendMoney);
        categoryRepository.save(expensesCategorySpendCash);
    }

    private void generateIncomeCategories() {
        Category incomeCategorySalary = new Category(transactionTypeIncome, "Job Salary");
        Category incomeCategoryFreelance = new Category(transactionTypeIncome, "Freelance Income");
        Category incomeCategoryRental = new Category(transactionTypeIncome, "Rental Income");
        Category InvestmentsInvestments = new Category(transactionTypeIncome, "Investments");
        Category incomeCategoryDonations = new Category(transactionTypeIncome, "Gifts & Donations");
        Category incomeCategoryBusiness = new Category(transactionTypeIncome, "Business");
        Category incomeCategoryReceiveMoney = new Category(transactionTypeIncome, "Recive money");
        Category incomeCategoryGetCash = new Category(transactionTypeIncome, "Get cash");
        categoryRepository.save(incomeCategorySalary);
        categoryRepository.save(incomeCategoryFreelance);
        categoryRepository.save(incomeCategoryRental);
        categoryRepository.save(InvestmentsInvestments);
        categoryRepository.save(incomeCategoryDonations);
        categoryRepository.save(incomeCategoryBusiness);
        categoryRepository.save(incomeCategoryReceiveMoney);
        categoryRepository.save(incomeCategoryGetCash);
    }

    private void generatePlans() {
        Subscription subscriptionFree = new Subscription(SubscriptionStatus.FREE, new BigDecimal(0), 3);
        Subscription subscriptionPlus = new Subscription(SubscriptionStatus.PLUS, new BigDecimal(5), 10);
        Subscription subscriptionPro = new Subscription(SubscriptionStatus.PRO, new BigDecimal(15), Integer.MAX_VALUE);
        subscriptionRepository.save(subscriptionFree);
        subscriptionRepository.save(subscriptionPlus);
        subscriptionRepository.save(subscriptionPro);
    }
}
