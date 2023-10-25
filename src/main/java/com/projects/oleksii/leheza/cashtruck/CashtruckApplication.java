package com.projects.oleksii.leheza.cashtruck;

import com.projects.oleksii.leheza.cashtruck.config.RandomUsersGenerator;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;
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
    CommandLineRunner commandLineRunner(RandomUsersGenerator randomUsersGenerator, ClientRepository clientRepository) {
        return args -> {
            randomUsersGenerator.generateRandomClients(30, clientRepository, getRandom(), dataFaker());
        };
    }

    //	@Bean
//	BCryptPasswordEncoder passwordEncoder(){
//		return new BCryptPasswordEncoder();
//	}
    @Bean
    @Scope("singleton")
    Random getRandom() {
        return new Random();
    }

    @Bean
    @Scope("prototype")
    Faker dataFaker() {
        return new Faker();
    }


}
