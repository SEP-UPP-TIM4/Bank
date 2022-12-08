package com.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    private String password;

    @Setter
    private String number;

    @Setter
    private String name;

    @Setter
    private String address;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @Setter
    private Set<CreditCard> creditCards = new HashSet<>();

    @Setter
    private BigDecimal amount;

    @Setter
    private BigDecimal reservedAmount;

    @Setter
    private Currency currency;
}
