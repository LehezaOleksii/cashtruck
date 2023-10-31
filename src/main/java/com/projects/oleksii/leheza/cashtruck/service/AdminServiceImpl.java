package com.projects.oleksii.leheza.cashtruck.service;

import com.projects.oleksii.leheza.cashtruck.domain.Admin;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.AdminDto;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.ManagerDto;
import com.projects.oleksii.leheza.cashtruck.enums.UserRole;
import com.projects.oleksii.leheza.cashtruck.repository.AdminRepository;
import com.projects.oleksii.leheza.cashtruck.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final ManagerRepository managerRepository;
    private final ClientService clientService;
    private final ManagerService managerService;

    @Override
    public void saveAdmin(AdminDto adminDto) throws IllegalStateException {
        if (existByEmail(adminDto.getEmail())) {
            throw new IllegalStateException("Email taken");
        }
        Admin admin = new Admin();
        admin.toBuilder()
                .firstname(adminDto.getFirstname())
                .lastname(adminDto.getLastname())
                .email(adminDto.getEmail())
                .password(adminDto.getPassword())
                .role(UserRole.Admin).build();
        adminRepository.save(admin);
    }

    @Override
    public List<Admin> findAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin findAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public void updateAdminInfo(Long id, AdminDto adminDto) {
        Admin currentAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Admin with id " + id + " not found"));
        String updatedEmail = adminDto.getEmail();
        String currentEmail = currentAdmin.getEmail();
        if (isEmailTaken(currentEmail, updatedEmail)) {
            throw new IllegalStateException("Admin with " + updatedEmail + " has already exist");
        }
        currentAdmin.toBuilder().firstname(adminDto.getFirstname())
                .lastname(adminDto.getLastname()).build();
        adminRepository.save(currentAdmin);
    }


    @Override
    public Manager findManagerByEmail(String email) throws IllegalStateException {
        return managerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Manager not found with email: " + email));
    }

    @Override
    public void createManager(ManagerDto managerDto) {
        managerService.saveManager(managerDto);
    }

    @Override
    public List<Manager> findAllManagers() {
        return managerService.findAllManagers();
    }

    @Override
    public void updateManagerInfo(Long managerId, ManagerDto managerDto) throws IllegalStateException {
        managerService.updateManagerInfo(managerId, managerDto);
    }

    @Override
    public void deleteManagerById(Long managerId) throws IllegalStateException {
        managerService.deleteManagerById(managerId);
    }

    @Override
    public void deleteClientById(Long clientId) {
        clientService.deleteById(clientId);
    }

    @Override
    public void changeClientInfoById(Long clientId, ClientDto clientDto) {
        clientService.updateClientInfo(clientId, clientDto);
    }

    @Override
    public void changeManagerInfoById(Long managerId, ManagerDto managerDto) {
        managerService.updateManagerInfo(managerId, managerDto);
    }

    private boolean existByEmail(String email) {
        return managerRepository.findByEmail(email).isPresent();
    }

    private boolean isEmailTaken(String currentEmail, String updatedEmail) {
        return !Objects.equals(currentEmail, updatedEmail) && existByEmail(updatedEmail);
    }
}
