package com.projects.oleksii.leheza.cashtruck.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonobankRequestPersonalClientDataDto {

    private String tokenRequestId;
    private String acceptUrl;
}
