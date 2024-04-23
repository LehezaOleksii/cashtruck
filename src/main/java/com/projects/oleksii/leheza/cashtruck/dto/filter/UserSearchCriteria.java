package com.projects.oleksii.leheza.cashtruck.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchCriteria {

    private String email;
    private String role;
    private String plan;
    private String status;
}
