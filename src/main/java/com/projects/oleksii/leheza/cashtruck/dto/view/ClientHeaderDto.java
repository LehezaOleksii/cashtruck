package com.projects.oleksii.leheza.cashtruck.dto.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class ClientHeaderDto {

    private Long id;
    private String avatar;
}
