package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.domain.Category;
import com.projects.oleksii.leheza.cashtruck.domain.Currency;
import com.projects.oleksii.leheza.cashtruck.domain.Subscription;
import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import com.projects.oleksii.leheza.cashtruck.repository.CategoryRepository;
import com.projects.oleksii.leheza.cashtruck.repository.CurrencyRepository;
import com.projects.oleksii.leheza.cashtruck.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MainEntitiesGenerator {

    private final CategoryRepository categoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CurrencyRepository currencyRepository;

    private final TransactionType transactionTypeUniversal = TransactionType.UNIVERSAL;
    private final TransactionType transactionTypeIncome = TransactionType.INCOME;


    public void generateMainEntities() {
        if (categoryRepository.findAll().isEmpty()) {
            generateExpensesCategories();
            generateIncomeCategories();
        }
        if (categoryRepository.findAll().isEmpty()) {
            generateExpensesCategories();
            generateIncomeCategories();
        }
        if (subscriptionRepository.findAll().isEmpty()) {
            generatePlans();
        }
        if (currencyRepository.findAll().isEmpty()) {
            generateCurrency();
        }
    }

    private void generateCurrency() {
        Currency usd = Currency.builder()
                .code(840)
                .name("United States dollar")
                .shortName("USD")
                .delimiter(100)
                .build();
        Currency uah = Currency.builder()
                .code(980)
                .name("Hryvnia")
                .shortName("UAH")
                .delimiter(100)
                .build();
        Currency eur = Currency.builder()
                .code(978)
                .name("Euro")
                .shortName("EUR")
                .delimiter(100)
                .build();
        currencyRepository.save(usd);
        currencyRepository.save(uah);
        currencyRepository.save(eur);
    }

    private void generateExpensesCategories() {

        Category categoryFood = new Category(
                transactionTypeUniversal,
                "Food, Beverage & Groceries",
                List.of(
                        "5411", "5412", "5422", "5451", "5441", "5462", "5499",
                        "5811", "5298", "5812", "5813", "5814", "5715", "5921", "5993"
                )
        );
        categoryRepository.save(categoryFood);

        Category retailCategory = new Category(
                transactionTypeUniversal,
                "Clothing & Accessories",
                List.of("5137", "5139", "5611", "5621", "5631", "5641", "5651", "5655",
                        "5661", "5691", "5699", "5944", "5977", "5978", "5262", "5297")
        );
        categoryRepository.save(retailCategory);

        Category homeAndFurnitureCategory = new Category(
                transactionTypeUniversal,
                "Home & Furniture",
                List.of("5021", "5200", "5712", "5713", "5714", "5718", "5719", "5950")
        );
        categoryRepository.save(homeAndFurnitureCategory);

        Category electronicsAndComputersCategory = new Category(
                transactionTypeUniversal,
                "Electronics & Computers",
                List.of("5045", "5732", "5946", "5997")
        );
        categoryRepository.save(electronicsAndComputersCategory);

        Category booksMediaCategory = new Category(transactionTypeUniversal, "Books & Media",
                List.of("5192", "5942")
        );
        categoryRepository.save(booksMediaCategory);

        Category automotiveAndTransportationCategory = new Category(
                transactionTypeUniversal,
                "Automotive & Transportation",
                List.of("5511", "5521", "5531", "5532", "5533", "7549", "5172", "5983",
                        "9752", "3351", "7512", "7513", "7519", "3000", "4111", "4112",
                        "4121", "4131", "4214", "4215", "4784", "4789", "4411", "4457",
                        "4468", "4511", "4582", "4722", "4723")
        );
        categoryRepository.save(automotiveAndTransportationCategory);

        Category homeImprovementCategory = new Category(
                transactionTypeUniversal,
                "Home Improvement & Construction",
                List.of("1520", "1711", "1731", "1740", "1750", "1761", "1771", "1799",
                        "5039", "5074", "5211", "5198", "0780")
        );
        categoryRepository.save(homeImprovementCategory);

        Category healthcareCategory = new Category(
                transactionTypeUniversal,
                "Healthcare & Medical",
                List.of("8011", "8021", "8031", "8041", "8042", "8043", "8049", "8062",
                        "8071", "8099", "5122", "5912", "5047", "5975", "5976", "4119",
                        "0742")
        );
        categoryRepository.save(healthcareCategory);

        Category financialServices = new Category(
                transactionTypeUniversal,
                "Financial Services & Insurance",
                List.of("6010", "6011", "6012", "6050", "6051", "6211", "6760", "6300",
                        "6381", "5960")
        );
        categoryRepository.save(financialServices);

        Category moneyTransfersPaymentOperationsCategory = new Category(
                transactionTypeUniversal,
                "Money Transfers & Payment Operations",
                List.of("4829", "6532", "6533", "6535", "6536", "6537", "6538", "6540", "6611")
        );
        categoryRepository.save(moneyTransfersPaymentOperationsCategory);

        Category professionalBusinessServicesCategory = new Category(
                transactionTypeUniversal,
                "Professional & Business Services",
                List.of("8111", "8911", "8931", "8999", "7311", "7333", "7338", "7339",
                        "7372", "7375", "7379", "7389", "7392", "7399", "6513", "9211",
                        "9222", "9223", "9311", "9399", "9402", "9405")
        );
        categoryRepository.save(professionalBusinessServicesCategory);

        Category entertainmentDigitalGoodsCategory = new Category(
                transactionTypeUniversal,
                "Entertainment & Digital Goods",
                List.of("7829", "7832", "7841", "7911", "7922", "7929", "7932", "7933",
                        "7941", "7991", "7992", "7996", "7998", "7999", "5815", "5816",
                        "5817", "5818", "7800", "7801", "7802", "7995", "9754", "5941",
                        "7993", "7994", "5970", "5971", "5972", "5973")
        );
        categoryRepository.save(entertainmentDigitalGoodsCategory);

        Category servicesMiscellaneousCategory = new Category(transactionTypeUniversal, "Services & Miscellaneous",
                List.of("7210", "7211", "7216", "7217", "7349", "7230", "7251", "7297",
                        "8050", "8211", "8220", "8241", "8244", "8249", "8299", "8351",
                        "5961", "5962", "5963", "5964", "5965", "5966", "5967", "5968",
                        "5969", "7622", "7623", "7629", "7631", "7641", "7692", "7699",
                        "7273", "7276", "7277", "7278", "7296", "7394", "7361", "7393",
                        "7395", "5935"));
        categoryRepository.save(servicesMiscellaneousCategory);

        Category technologyAndTelecommunicationsCategory = new Category(transactionTypeUniversal, "Technology & Telecommunications",
                List.of("4812", "4813", "4814", "4815", "4816", "4821", "4899", "5734"));
        categoryRepository.save(technologyAndTelecommunicationsCategory);

        Category nonProfitCharitableAndPoliticalCategory = new Category(transactionTypeUniversal, "Non-Profit, Charitable & Political",
                List.of("8398", "8641", "8699", "8651", "8661"));
        categoryRepository.save(nonProfitCharitableAndPoliticalCategory);

        Category mediaPublishingCategory = new Category(transactionTypeUniversal, "Media & Publishing",
                List.of("2741", "2791"));
        categoryRepository.save(mediaPublishingCategory);

        Category wholesaleCorporateCategory = new Category(transactionTypeUniversal, "Wholesale & Corporate",
                List.of("5300", "9950"));
        categoryRepository.save(wholesaleCorporateCategory);

        Category uncategorizedIncomeCategory = new Category(transactionTypeIncome, "Uncategorized income",
                List.of("4304"));
        categoryRepository.save(uncategorizedIncomeCategory);

        Category uncategorizedExpenseCategory = new Category(transactionTypeIncome, "Uncategorized expense",
                List.of("4304"));
        categoryRepository.save(uncategorizedExpenseCategory);
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
        Subscription subscriptionFree = new Subscription(SubscriptionStatus.FREE, 0, 3);
        Subscription subscriptionPlus = new Subscription(SubscriptionStatus.PLUS, 500, 10);
        Subscription subscriptionPro = new Subscription(SubscriptionStatus.PRO, 1500, Integer.MAX_VALUE);
        subscriptionRepository.save(subscriptionFree);
        subscriptionRepository.save(subscriptionPlus);
        subscriptionRepository.save(subscriptionPro);
    }
}
