package com.projects.oleksii.leheza.cashtruck.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@Entity
@Table(name = "plans")
@NoArgsConstructor
public class Plan {

    @Id
    @SequenceGenerator(name = "plan_sequence", sequenceName = "plan_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plan_sequence")
    private Long id;
    private String name;
}
