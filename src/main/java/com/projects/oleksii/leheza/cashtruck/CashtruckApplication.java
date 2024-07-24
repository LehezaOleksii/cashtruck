package com.projects.oleksii.leheza.cashtruck;

import com.projects.oleksii.leheza.cashtruck.config.MainEntitiesGenerator;
import com.projects.oleksii.leheza.cashtruck.config.RandomUsersGenerator;
import com.projects.oleksii.leheza.cashtruck.service.implemintation.UserServiceImpl;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.EmailService;
import com.projects.oleksii.leheza.cashtruck.service.subscription.SubscriptionChecker;
import com.stripe.Stripe;
import net.datafaker.Faker;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Random;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class CashtruckApplication {

    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecret;

    public static void main(String[] args) {
        SpringApplication.run(CashtruckApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(RandomUsersGenerator randomUsersGenerator, MainEntitiesGenerator mainEntitiesGenerator, UserServiceImpl userService, EmailService emailService) {
        return args -> {
            mainEntitiesGenerator.generateMainEntities();
            randomUsersGenerator.generateRandomClientFields(10, 5, 1, 32, 200);
            SubscriptionChecker subscriptionChecker = new SubscriptionChecker(userService, emailService);
            subscriptionChecker.checkAndUpdateSubscriptionStatus();
            Stripe.apiKey = stripeSecret;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Scope("prototype")
    public Random getRandom() {
        return new Random();
    }

    @Bean
    public Faker dataFaker() {
        return new Faker();
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
