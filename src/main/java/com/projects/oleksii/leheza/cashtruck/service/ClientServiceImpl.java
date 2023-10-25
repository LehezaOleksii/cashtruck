package com.projects.oleksii.leheza.cashtruck.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.repository.ClientRepository;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService, UserDetailsService {

    private ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client client = clientRepository.findByEmail(email);
        if (client == null) {
            throw new UsernameNotFoundException("User with email " + email + " not found");
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(client.getRole().name()));
        return new User(client.getEmail(), client.getPassword(), authorities);
    }

    @Override
    public void saveClient(Client client) {
        if (existByEmail(client)) {
            throw new IllegalStateException("Email taken");
        }
        client.setPassword(passwordEncoder.encode(client.getPassword()));
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
    public void deleteById(Long clientId) {
        boolean exists = clientRepository.existsById(clientId);
        if (!exists) {
            throw new IllegalStateException("Client with" + clientId + "does not exists");
        }
        clientRepository.deleteById(clientId);
    }

    @Override
    @Transactional
    public void updateClient(Long clientId, Client client) {
        String updatedEmail = client.getEmail();
        Optional<Client> currentClientOptional = clientRepository.findById(clientId);
        if (existByEmail(client) && currentClientOptional.get().getEmail() != updatedEmail) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        Client currentClient = currentClientOptional.get().toBuilder().firstname(client.getFirstname())
                .lastname(client.getLastname()).income(client.getIncome()).expenses(client.getExpenses())
                .email(updatedEmail).build();
        clientRepository.save(currentClient);
    }

    @Override
    public Client getClient(Long clientId) {
        return clientRepository.getReferenceById(clientId);
    }

    private boolean existByEmail(Client client) {
        Client clientInRepository = clientRepository.findByEmail(client.getEmail());
        if (clientInRepository!=null) {
            return true;
        } else {
            return false;
        }
    }
}
