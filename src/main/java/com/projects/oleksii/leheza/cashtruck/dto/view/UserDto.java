package com.projects.oleksii.leheza.cashtruck.dto.view;

import com.projects.oleksii.leheza.cashtruck.domain.Saving;
import com.projects.oleksii.leheza.cashtruck.domain.Transaction;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class UserDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String country;
    private String language;
    private Saving saving;
    private String avatar;
    private String role;
    private String status;
    private List<Transaction> incomes;
    private List<Transaction> expenses;
}
