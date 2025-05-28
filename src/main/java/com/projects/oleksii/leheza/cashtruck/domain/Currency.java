package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder(toBuilder = true)
@Table(name = "Currencies")
public class Currency {

    @Id
    @SequenceGenerator(name = "currency_sequence", sequenceName = "currency_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currency_sequence")
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(length = 3, nullable = false)
    private String shortName;
    @Column(nullable = false, unique = true)
    private int code;
    @Column(nullable = false)
    private int delimiter;
}
