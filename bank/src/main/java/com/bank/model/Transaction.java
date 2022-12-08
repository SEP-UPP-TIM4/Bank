package com.bank.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;             //acquirer/issuer(ako je banka kupca) order id

    @Setter
    @Nullable
    private UUID issuerId;

    @Setter
    private UUID acquirerId;

    @Setter
    private BigDecimal amount;

    @Setter
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Setter
    private LocalDateTime timeStamp;

    @Setter
    @Builder.Default
    private boolean processed = false;

    @PrePersist
    protected void onCreate() {
        timeStamp = LocalDateTime.now();
    }
}
