package com.projects.oleksii.leheza.cashtruck.config;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;
import net.datafaker.Faker;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Configuration
public class RandomUsersGenerator {

    public void generateRandomClients(Integer size, ClientRepository repository, Random random, Faker faker) {
        List<String> firstnames = Stream.generate(() -> faker.name().firstName()).distinct().limit(size).toList();
        List<String> lastnames = Stream.generate(() -> faker.name().lastName()).distinct().limit(size).toList();
        List<UserRole> roles = Arrays.stream(UserRole.values()).toList();
        IntStream.range(1, size).mapToObj(index -> Client.builder()
                .firstname(firstnames.get(index))
                .lastname(lastnames.get(index))
                .email(faker.internet().emailAddress())
                .password(faker.lorem().sentence(2))
                .role(roles.get(random.nextInt(roles.size()))).build()).forEach(repository::save);
    }
}
