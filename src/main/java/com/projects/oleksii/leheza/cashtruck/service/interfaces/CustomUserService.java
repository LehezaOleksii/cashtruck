package com.projects.oleksii.leheza.cashtruck.service.interfaces;

import com.projects.oleksii.leheza.cashtruck.domain.CustomUser;
import com.projects.oleksii.leheza.cashtruck.dto.view.CustomUserDto;

import java.util.List;

public interface CustomUserService {
     List<CustomUserDto> findAll();
}
