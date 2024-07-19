package com.projects.oleksii.leheza.cashtruck.dto.create;

import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CreateCategoryDto {

    private Long id;
    private String categoryName;
    private TransactionType transactionType;
}
