package com.projects.oleksii.leheza.cashtruck.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ClientStatisticDto {
    @Pattern(regexp = "^[0-9]")
    private BigDecimal expenses;
    @Pattern(regexp = "^[0-9]")
    private BigDecimal income;
    @Pattern(regexp = "^[0-9]")
    private BigDecimal profit;
    @Pattern(regexp = "^[0-9]")
    private BigDecimal saving;
}
