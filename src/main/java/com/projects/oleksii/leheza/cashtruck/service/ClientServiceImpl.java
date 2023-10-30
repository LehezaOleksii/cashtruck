package com.projects.oleksii.leheza.cashtruck.service;

import java.util.List;
import java.util.Objects;

import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
//    private final PasswordEncoder passwordEncoder;


//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Client client = clientRepository.findByEmail(email);
//        if (client == null) {
//            throw new UsernameNotFoundException("User with email " + email + " not found");
//        }
//        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(client.getRole().name()));
//        return new User(client.getEmail(), client.getPassword(), authorities);
//    }

    @Override
    public void saveClient(ClientDto clientDto) {
        if (existByEmail(clientDto.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
//        client.setPassword(passwordEncoder.encode(client.getPassword()));
        Client client = new Client();
        client.toBuilder()
                .firstname(clientDto.getFirstname())
                .lastname(clientDto.getLastname())
                .email(clientDto.getEmail())
                .password(clientDto.getPassword())
                .role(UserRole.Client).build();
        clientRepository.save(client);
    }

    @Override
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }


    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public void deleteById(Long clientId) throws IllegalStateException {
        boolean exists = clientRepository.existsById(clientId);
        if (!exists) {
            throw new IllegalStateException("Client with" + clientId + "does not exists");
        }
        clientRepository.deleteById(clientId);
    }

    @Override
    public void updateClientInfo(Long clientId, ClientDto clientDto) throws IllegalStateException {
        Client currentClient = clientRepository.findById(clientId).get();
        String updatedEmail = clientDto.getEmail();
        String currentEmail = currentClient.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        currentClient.toBuilder().firstname(clientDto.getFirstname())
                .lastname(clientDto.getLastname()).build();
        clientRepository.save(currentClient);
    }

    @Override
    @Transactional
    public void updateClient(Long clientId, Client client) throws IllegalStateException {
        Client currentClient = clientRepository.findById(clientId).get();
        String updatedEmail = client.getEmail();
        String currentEmail = currentClient.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        currentClient.toBuilder().firstname(client.getFirstname())
                .lastname(client.getLastname()).income(client.getIncome()).expenses(client.getExpenses())
                .email(updatedEmail).build();
        clientRepository.save(currentClient);
    }

    @Override
    public Client getClient(Long clientId) {
        return clientRepository.getReferenceById(clientId);
    }

    private boolean existByEmail(String email) {
        return clientRepository.findByEmail(email) != null;
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }
}
