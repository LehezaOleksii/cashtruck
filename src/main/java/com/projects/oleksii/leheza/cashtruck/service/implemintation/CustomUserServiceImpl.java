package com.projects.oleksii.leheza.cashtruck.service.implemintation;

import com.projects.oleksii.leheza.cashtruck.dto.DtoMapper;
import com.projects.oleksii.leheza.cashtruck.dto.view.CustomUserDto;
import com.projects.oleksii.leheza.cashtruck.repository.CustomUserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.CustomUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl implements CustomUserService {

    private final CustomUserRepository customUserRepository;
    private final DtoMapper dtoMapper;

    @Override
    public List<CustomUserDto> findAll() {
        return customUserRepository.findAll().stream()
                .map(dtoMapper::customUserToCustomUserDto)
                .toList();
    }
}
