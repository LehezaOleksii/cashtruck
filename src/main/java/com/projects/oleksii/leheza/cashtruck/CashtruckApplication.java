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
    CommandLineRunner runner(RandomUsersGenerator randomUsersGenerator, MainEntitiesGenerator mainEntitiesGenerator) {
        return args -> {
            mainEntitiesGenerator.generateMainEntities();
            randomUsersGenerator.generateRandomUsers(10,30,5,1,200);
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
    Faker dataFaker() {
        return new Faker();
    }
}
