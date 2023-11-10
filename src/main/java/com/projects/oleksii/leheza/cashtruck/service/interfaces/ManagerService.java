package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Client;
import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.ClientDto;
import com.projects.oleksii.leheza.cashtruck.dto.ManagerDto;
import com.projects.oleksii.leheza.cashtruck.repository.ManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ManagerService {

    public void saveManager(ManagerDto managerDto);

    public void saveManager(Manager manager);

    public List<Manager> findAllManagers();

    public Manager findManagerByEmail(String email);

    public Manager findManagerById(Long managerId);

    public void updateManagerInfo(Long managerId, ManagerDto managerDto);

    public void deleteManagerById(Long id);
}
