package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import com.projects.oleksii.leheza.cashtruck.domain.Image;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateManagerDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.ManagerUpdateDto;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import com.projects.oleksii.leheza.cashtruck.repository.CustomUserRepository;
import com.projects.oleksii.leheza.cashtruck.repository.ImageRepository;
import com.projects.oleksii.leheza.cashtruck.repository.ManagerRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ManagerService;
import com.projects.oleksii.leheza.cashtruck.util.ImageConvertor;
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
    private final ImageRepository imageRepository;
    private final ImageConvertor imageConvertor;

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
    public void updateManagerInfo(Long managerId, ManagerUpdateDto managerUpdateDto) throws IllegalStateException {
        Manager currentManager = managerRepository.findById(managerId).get();
        String updatedEmail = managerUpdateDto.getEmail();
        String currentEmail = currentManager.getCustomUser().getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Client with " + updatedEmail + " has already exist");
        }
        CustomUser user = currentManager.getCustomUser().toBuilder()
                .phoneNumber(managerUpdateDto.getPhoneNumber())
                .country(managerUpdateDto.getCountry())
                .language(managerUpdateDto.getLanguage())
                .firstName(managerUpdateDto.getFirstName())
                .lastName(managerUpdateDto.getLastName())
                .email(managerUpdateDto.getEmail())
                .password(managerUpdateDto.getPassword())
                .avatar(imageRepository.save(new Image(imageConvertor.convertStringToByteImage(managerUpdateDto.getAvatar()))))
                .role(UserRole.Client)
                .build();
        customUserRepository.save(user);
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
        return managerRepository.findManagerByCustomUser_Email(email) != null && email.equals(managerRepository.findManagerByCustomUser_Email(email).getCustomUser().getEmail());
//        managerRepository.findManagerByUser_Email(email).isPresent()
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }
}
