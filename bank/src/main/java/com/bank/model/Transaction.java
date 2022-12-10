package com.bank.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @ManyToOne
    @JoinColumn
    private Account issuer;

    @Setter
    @ManyToOne
    @JoinColumn
    private Account acquirer;

    @Setter
    private BigDecimal amount;

    @Setter
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Setter
    private LocalDateTime timeStamp;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        timeStamp = LocalDateTime.now();
    }
}
