package com.projects.oleksii.leheza.cashtruck.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.projects.oleksii.leheza.cashtruck.entity.Client;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;

@Configuration
public class ClientConfiguration {
	@Bean
	CommandLineRunner commandLineRunner(ClientRepository repository) {
		return args -> {
			Client cleint1 = new Client(null, "first name 1", "last name 1", "email 1", null, null);
			Client cleint2 = new Client(null, "first name 2", "last name 2", "email 2", null, null);
			repository.saveAll(List.of(cleint1, cleint2));
		};
	}
}
