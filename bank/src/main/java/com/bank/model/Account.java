package com.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @Setter
    private String password;
    @Setter
    private String number;
    @Setter
    private String name;
    @Setter
    private String address;
    @OneToMany
    private Set<CreditCard> creditCards;
    @Setter
    private double amount;
    @Setter
    private double reservedAmount;
    @Setter
    private Currency currency;
}
