package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Admin;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.AdminDto;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.ManagerDto;

import java.util.List;


public interface AdminService {

    void saveAdmin(AdminDto adminDto);

    void saveAdmin(Admin admin);

    List<Manager> findAllManagers();

    List<Admin> findAllAdmins();

    Admin findAdminByEmail(String email);

    void updateAdminInfo(Long id, AdminDto adminDto);

    Manager findManagerByEmail(String email) throws IllegalStateException;

    void createManager(ManagerDto managerDto);

    void updateManagerInfo(Long id,ManagerDto managerDto);

    void deleteManagerById(Long id);

    void deleteClientById(Long id);

    void changeClientInfoById(Long clientId, ClientDto clientDto);

    void changeManagerInfoById(Long managerId, ManagerDto managerDto);
}
