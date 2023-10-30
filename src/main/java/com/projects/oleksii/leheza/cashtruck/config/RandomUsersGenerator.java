package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.domain.Admin;
import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import com.projects.oleksii.leheza.cashtruck.repository.AdminRepository;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;
import com.projects.oleksii.leheza.cashtruck.repository.ManagerRepository;
import net.datafaker.Faker;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Configuration
public class RandomUsersGenerator {


    public void generateRandomUsers(Integer clientsNumber,Integer managersNumber,Integer adminNumber,
                                    ClientRepository clientRepository,ManagerRepository managerRepository,
                                    AdminRepository adminRepository, Faker faker){
        generateRandomClients(clientsNumber,clientRepository,faker);
        generateRandomManagers(managersNumber,managerRepository,faker);
        generateRandomAdmins(adminNumber,adminRepository,faker);
    }
    private void generateRandomClients(Integer size, ClientRepository clientRepository, Faker faker) {
        List<String> firstnames = Stream.generate(() -> faker.name().firstName()).distinct().limit(size).toList();
        List<String> lastnames = Stream.generate(() -> faker.name().lastName()).distinct().limit(size).toList();
        IntStream.range(1, size).mapToObj(index -> Client.builder()
                .firstname(firstnames.get(index))
                .lastname(lastnames.get(index))
                .email(faker.internet().emailAddress())
                .password(faker.lorem().sentence(2))
                .role(UserRole.Client).build()).forEach(clientRepository::save);
    }

    private void generateRandomManagers(Integer size, ManagerRepository managerRepository, Faker faker) {
        List<String> firstnames = Stream.generate(() -> faker.name().firstName()).distinct().limit(size).toList();
        List<String> lastnames = Stream.generate(() -> faker.name().lastName()).distinct().limit(size).toList();
        IntStream.range(1, size).mapToObj(index -> Manager.builder()
                .firstname(firstnames.get(index))
                .lastname(lastnames.get(index))
                .email(faker.internet().emailAddress())
                .password(faker.lorem().sentence(2))
                .role(UserRole.Manager).build()).forEach(managerRepository::save);
    }

    private void generateRandomAdmins(Integer size, AdminRepository adminRepository, Faker faker) {
        List<String> firstnames = Stream.generate(() -> faker.name().firstName()).distinct().limit(size).toList();
        List<String> lastnames = Stream.generate(() -> faker.name().lastName()).distinct().limit(size).toList();
        IntStream.range(1, size).mapToObj(index -> Admin.builder()
                .firstname(firstnames.get(index))
                .lastname(lastnames.get(index))
                .email(faker.internet().emailAddress())
                .password(faker.lorem().sentence(2))
                .role(UserRole.Admin).build()).forEach(adminRepository::save);
    }
}
