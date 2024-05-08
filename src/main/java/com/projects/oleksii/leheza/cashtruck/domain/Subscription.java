package com.projects.oleksii.leheza.cashtruck.domain;

import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @SequenceGenerator(name = "subscription_sequence", sequenceName = "subscription_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_sequence")
    private Long id;
    private SubscriptionStatus subscriptionStatus;
    private BigDecimal price;
    private int maxCardsSupport;

    public Subscription(SubscriptionStatus subscriptionStatus, BigDecimal price, int maxCardsSupport) {
        this.subscriptionStatus = subscriptionStatus;
        this.price = price;
        this.maxCardsSupport = maxCardsSupport;
    }
}
