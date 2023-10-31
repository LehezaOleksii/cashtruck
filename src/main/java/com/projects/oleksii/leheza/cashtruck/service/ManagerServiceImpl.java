package com.projects.oleksii.leheza.cashtruck.service;

import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.ManagerDto;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import com.projects.oleksii.leheza.cashtruck.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;

    @Override
    public void saveManager(ManagerDto managerDto) {
        if (existByEmail(managerDto.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
        Manager manager = new Manager();
        manager.toBuilder()
                .firstname(managerDto.getFirstname())
                .lastname(managerDto.getLastname())
                .email(managerDto.getEmail())
                .password(managerDto.getPassword())
                .role(UserRole.Manager).build();
        managerRepository.save(manager);
    }

    @Override
    public List<Manager> findAllManagers() {
        return managerRepository.findAll();//TODO find all - me
    }

    @Override
    public Manager findManagerByEmail(String email) throws IllegalStateException {
        return managerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Manager not found with email: " + email));
    }

    @Override
    public Manager findManagerById(Long managerId) throws IllegalStateException {
        Optional<Manager> managerOptional = managerRepository.findById(managerId);
        if (!managerOptional.isPresent()) {
            throw new IllegalStateException("Manager with id " + managerId + "doesn`t exist");
        }
        return managerOptional.get();
    }

    @Override
    public void updateManagerInfo(Long managerId, ManagerDto managerDto) throws IllegalStateException {
        Manager currentManager = managerRepository.findById(managerId)
                .orElseThrow(() -> new IllegalStateException("Manager with id " + managerId + " not found"));
        String updatedEmail = managerDto.getEmail();
        String currentEmail = currentManager.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        currentManager.toBuilder().firstname(managerDto.getFirstname())
                .lastname(managerDto.getLastname()).build();
        managerRepository.save(currentManager);
    }

    @Override
    public void deleteManagerById(Long id) throws IllegalStateException {
        Optional<Manager> manager = managerRepository.findById(id);
        if (manager.isPresent()) {
            managerRepository.deleteById(id);
        } else {
            throw new IllegalStateException("Manager with ID " + id + " doesn't exist");
        }
    }

    private boolean existByEmail(String email) {
        return managerRepository.findByEmail(email).isPresent();
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }
}
