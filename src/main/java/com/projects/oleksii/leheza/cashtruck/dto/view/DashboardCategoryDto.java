package com.projects.oleksii.leheza.cashtruck.dto.view;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class DashboardCategoryDto {

    private String categoryName;
    private long sum;
}
