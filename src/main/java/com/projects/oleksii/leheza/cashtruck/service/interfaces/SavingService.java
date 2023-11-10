package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.BankCard;
import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import com.projects.oleksii.leheza.cashtruck.dto.ClientStatisticDto;

import java.util.Collection;
import java.util.List;

public interface SavingService {

    public List<Saving> findAll();

    public void saveAll(Collection<Saving>savings);

}
