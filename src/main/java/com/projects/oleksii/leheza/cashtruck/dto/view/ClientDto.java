package com.projects.oleksii.leheza.cashtruck.dto.view;

import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class ClientDto {

    private final Long id;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final Saving saving;
    private final List<TransactionDto> incomes;
    private final List<TransactionDto> expenses;
}
