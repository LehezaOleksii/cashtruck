package com.projects.oleksii.leheza.cashtruck.domain;

import com.projects.oleksii.leheza.cashtruck.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @SequenceGenerator(name = "subscription_sequence", sequenceName = "subscription_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_sequence")
    private Long id;
    @Column(name = "subscription_status")
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;
    private BigDecimal price;
    @Column(name = "max_card_support")
    private int maxCardsSupport;
    @OneToMany(mappedBy = "subscription")
    private Set<User> users;

    public Subscription(SubscriptionStatus subscriptionStatus, BigDecimal price, int maxCardsSupport) {
        this.subscriptionStatus = subscriptionStatus;
        this.price = price;
        this.maxCardsSupport = maxCardsSupport;
    }
}
