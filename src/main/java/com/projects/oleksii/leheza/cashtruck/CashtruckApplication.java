package com.projects.oleksii.leheza.cashtruck;

import com.projects.oleksii.leheza.cashtruck.config.MainEntitiesGenerator;
import com.projects.oleksii.leheza.cashtruck.config.RandomUsersGenerator;
import com.projects.oleksii.leheza.cashtruck.repository.*;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Random;

@SpringBootApplication
public class CashtruckApplication {

    public static void main(String[] args) {
        SpringApplication.run(CashtruckApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(RandomUsersGenerator randomUsersGenerator, MainEntitiesGenerator mainEntitiesGenerator,
                             ClientRepository clientRepository,
                             ManagerRepository managerRepository,
                             AdminRepository adminRepository,
                             IncomeCategoryRepository incomeCategoryRepository,
                             ExpensesCategoryRepository expensesCategoryRepository) {
        return args -> {
            mainEntitiesGenerator.generateMainEntities(expensesCategoryRepository, incomeCategoryRepository);
            randomUsersGenerator.generateRandomUsers(100,300,10,1,1000);
        };
    }

    //	@Bean
//	BCryptPasswordEncoder passwordEncoder(){
//		return new BCryptPasswordEncoder();
//	}
    @Bean
    @Scope("prototype")
    Random getRandom() {
        return new Random();
    }

    @Bean
    @Scope("prototype")
    Faker dataFaker() {
        return new Faker();
    }
}
