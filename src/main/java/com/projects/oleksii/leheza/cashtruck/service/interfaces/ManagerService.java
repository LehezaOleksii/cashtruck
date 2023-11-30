package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.ManagerDto;
import com.projects.oleksii.leheza.cashtruck.repository.ManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ManagerService {

    void saveManager(ManagerDto managerDto);

    void saveManager(Manager manager);

    List<Manager> findAllManagers();

    Manager findManagerByEmail(String email);

    Manager findManagerById(Long managerId);

    void updateManagerInfo(Long managerId, ManagerDto managerDto);

    void deleteManagerById(Long id);
}
