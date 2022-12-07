package com.bank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

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
    private UUID id;
    @Setter
    private String issuerAcountNumber;
    @Setter
    private String acquirerAccountNumber;
    @Setter
    private double amount;
    @Setter
    private LocalDateTime timeStamp;
    @Setter
    private Currency currency;
}
