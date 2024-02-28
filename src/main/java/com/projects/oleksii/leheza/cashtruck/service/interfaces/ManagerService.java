package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.Manager;
import com.projects.oleksii.leheza.cashtruck.dto.create.CreateManagerDto;
import com.projects.oleksii.leheza.cashtruck.dto.update.ManagerUpdateDto;

import java.util.List;

public interface ManagerService {

    void saveManager(CreateManagerDto createManagerDto);

    void saveManager(Manager manager);

    List<Manager> findAllManagers();

    Manager findManagerByEmail(String email);

    Manager findManagerById(Long managerId);

    void updateManagerInfo(Long managerId, ManagerUpdateDto managerUpdateDto);

    void deleteManagerById(Long id);
}
