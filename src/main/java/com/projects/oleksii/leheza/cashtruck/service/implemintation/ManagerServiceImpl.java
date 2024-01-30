package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateManagerDto;
import com.projects.oleksii.leheza.cashtruck.repository.ManagerRepository;
import com.projects.oleksii.leheza.cashtruck.repository.CustomUserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final CustomUserRepository customUserRepository;

    @Override
    public void saveManager(CreateManagerDto createManagerDto) {
        if (existByEmail(createManagerDto.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
        CustomUser customUser = CustomUser.builder()
                .firstName(createManagerDto.getFirstname())
                .lastName(createManagerDto.getLastname())
                .email(createManagerDto.getEmail())
                .password(createManagerDto.getPassword())
                .build();
        Manager manager = Manager.builder()
                .customUser(customUser)
                .build();
        managerRepository.save(manager);
    }

    @Override
    public void saveManager(Manager manager) {
        if (!Optional.ofNullable(manager).isPresent()) {
            throw new IllegalStateException("Manager is empty");
        }
        if (existByEmail(manager.getCustomUser().getEmail())) {
            throw new IllegalStateException("Email taken");
        }
        managerRepository.save(manager);
    }

    @Override
    public List<Manager> findAllManagers() {
        return managerRepository.findAll();//TODO find all - me
    }

    @Override
    public Manager findManagerByEmail(String email) throws IllegalStateException {
        return managerRepository.findManagerByCustomUser_Email(email);
//                .orElseThrow(() -> new IllegalStateException("Manager not found with email: " + email)
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
    public void updateManagerInfo(Long managerId, CreateManagerDto createManagerDto) throws IllegalStateException {
        Manager currentManager = managerRepository.findById(managerId)
                .orElseThrow(() -> new IllegalStateException("Manager with id " + managerId + " not found"));
        String updatedEmail = createManagerDto.getEmail();
        String currentEmail = currentManager.getCustomUser().getEmail();
        CustomUser currentCustomUser = customUserRepository.getReferenceById(managerId);
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        currentCustomUser.toBuilder()
                .firstName(createManagerDto.getFirstname())
                .lastName(createManagerDto.getLastname())
                .build();
        customUserRepository.save(currentCustomUser);
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
        return managerRepository.findManagerByCustomUser_Email(email)!=null&&email.equals(managerRepository.findManagerByCustomUser_Email(email).getCustomUser().getEmail());
//        managerRepository.findManagerByUser_Email(email).isPresent()
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }
}
