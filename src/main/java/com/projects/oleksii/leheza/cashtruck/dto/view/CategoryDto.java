package com.projects.oleksii.leheza.cashtruck.dto.view;

import com.projects.oleksii.leheza.cashtruck.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class CategoryDto {

    @Enumerated(EnumType.ORDINAL)
    private TransactionType transactionType;
    @NotEmpty
    @NotBlank
    @Column(name = "name", length = 50)
    private String name;
    private String categoryImage;
}
