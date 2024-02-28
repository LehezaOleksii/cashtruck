package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.domain.Admin;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateAdminDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateManagerDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.ManagerUpdateDto;
import com.projects.oleksii.leheza.cashtruck.repository.AdminRepository;
import com.projects.oleksii.leheza.cashtruck.repository.ManagerRepository;
import com.projects.oleksii.leheza.cashtruck.repository.CustomUserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.AdminService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ClientService;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CustomUserRepository customUserRepository;
    private final AdminRepository adminRepository;
    private final ManagerRepository managerRepository;
    private final ClientService clientService;
    private final ManagerService managerService;

    @Override
    public void saveAdmin(CreateAdminDto createAdminDto) throws IllegalStateException {
        if (existByEmail(createAdminDto.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
        CustomUser customUser = CustomUser.builder()
                .firstName(createAdminDto.getFirstname())
                .lastName(createAdminDto.getLastname())
                .password(createAdminDto.getPassword())
                .email(createAdminDto.getEmail())
                .build();
        Admin admin = Admin.builder()
                .customUser(customUser)
                .build();
        adminRepository.save(admin);
    }

    @Override
    public void saveAdmin(Admin admin) {
        if (!Optional.ofNullable(admin).isPresent()) {
            throw new IllegalStateException("Admin is empty");
        }
        if (existByEmail(admin.getCustomUser().getEmail())) {
            throw new IllegalStateException("Email taken");
        }
        adminRepository.save(admin);
    }

    @Override
    public List<Admin> findAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin findAdminByEmail(String email) {
        return adminRepository.findAdminByCustomUser_Email(email);
    }

    @Override
    public void updateAdminInfo(Long id, CreateAdminDto createAdminDto) {
        Admin currentAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Admin with id " + id + " not found"));
        String updatedEmail = createAdminDto.getEmail();
        String currentEmail = currentAdmin.getCustomUser().getEmail();
        CustomUser currentCustomUser = currentAdmin.getCustomUser();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Admin with " + updatedEmail + " has already exist");
        }
        currentCustomUser.toBuilder()
                .firstName(createAdminDto.getFirstname())
                .lastName(createAdminDto.getLastname()).build();
        customUserRepository.save(currentCustomUser);
    }

    @Override
    public void createManager(CreateManagerDto createManagerDto) {
        managerService.saveManager(createManagerDto);
    }

    @Override
    public List<Manager> findAllManagers() {
        return managerService.findAllManagers();
    }

//    @Override
//    public void updateManagerInfo(Long managerId, CreateManagerDto createManagerDto) throws IllegalStateException {
//        managerService.updateManagerInfo(managerId, createManagerDto);
//    }

    @Override
    public void deleteManagerById(Long managerId) throws IllegalStateException {
        managerService.deleteManagerById(managerId);
    }

    @Override
    public void deleteClientById(Long clientId) {
        clientService.deleteById(clientId);
    }

    @Override
    public void changeClientInfoById(Long clientId, CreateClientDto createClientDto) {
//        clientService.updateClientInfo(clientId, createClientDto);
    }

    @Override
    public void changeManagerInfoById(Long managerId, ManagerUpdateDto managerUpdateDto) {
        managerService.updateManagerInfo(managerId, managerUpdateDto);
    }

    private boolean existByEmail(String email) {
        return email.equals(adminRepository.findAdminByCustomUser_Email(email).getCustomUser().getEmail());
//        return adminRepository.findAdminByUser_Email(email).isPresent();
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }
}
