package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Admin;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateAdminDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateManagerDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.ManagerUpdateDto;

import java.util.List;


public interface AdminService {

    void saveAdmin(CreateAdminDto createAdminDto);

    void saveAdmin(Admin admin);

    List<Manager> findAllManagers();

    List<Admin> findAllAdmins();

    Admin findAdminByEmail(String email);

    void updateAdminInfo(Long id, CreateAdminDto createAdminDto);

//    Manager findManagerByEmail(String email) throws IllegalStateException;

    void createManager(CreateManagerDto createManagerDto);

//    void updateManagerInfo(Long id, CreateManagerDto createManagerDto);

    void deleteManagerById(Long id);

    void deleteClientById(Long id);

    void changeClientInfoById(Long clientId, CreateClientDto createClientDto);

    void changeManagerInfoById(Long managerId, ManagerUpdateDto managerUpdateDto);
}
