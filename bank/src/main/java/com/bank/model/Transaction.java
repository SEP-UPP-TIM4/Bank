package com.bank.model;

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
    private Long id;                        //acquirer/issuer order id

    @Setter
    private LocalDateTime timestamp;        //acquirer/issuer timestamp

    @Setter
    private BigDecimal amount;

    @Setter
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    @Setter
    @ManyToOne
    @JoinColumn(name = "account_number", referencedColumnName = "number")
    private Account account;

    @Setter
    private boolean income;     // da li je priliv (true) ili odliv (false) novca
}
