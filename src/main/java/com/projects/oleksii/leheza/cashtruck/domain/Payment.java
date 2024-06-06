package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String number;
    private String email;
    private String address;
    private long billValue;
    private String cardNumber;
    private String cardHolder;
    private String dateValue;
    private String cvc;
}