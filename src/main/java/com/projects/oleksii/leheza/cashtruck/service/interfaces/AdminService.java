package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Admin;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.AdminDto;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.ManagerDto;

import java.util.List;


public interface AdminService {

    public void saveAdmin(AdminDto adminDto);

    public void saveAdmin(Admin admin);

    List<Manager> findAllManagers();

    public List<Admin> findAllAdmins();

    public Admin findAdminByEmail(String email);

    public void updateAdminInfo(Long id, AdminDto adminDto);

    Manager findManagerByEmail(String email) throws IllegalStateException;

    public void createManager(ManagerDto managerDto);

    public void updateManagerInfo(Long id,ManagerDto managerDto);

    public void deleteManagerById(Long id);

    public void deleteClientById(Long id);

    public void changeClientInfoById(Long clientId, ClientDto clientDto);

    public void changeManagerInfoById(Long managerId, ManagerDto managerDto);
}