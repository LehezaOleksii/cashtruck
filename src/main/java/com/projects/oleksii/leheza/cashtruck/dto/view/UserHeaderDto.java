package com.projects.oleksii.leheza.cashtruck.dto.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserHeaderDto {

    private Long id;
    private String avatar;
}
