package com.bank.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.springframework.lang.Nullable;

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
    private TransactionStatus status = TransactionStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    @Setter
    @ManyToOne
    @Nullable
    @JoinColumn(name = "account_number", referencedColumnName = "number")
    private Account account;

    @Setter
    private boolean income;     // da li je priliv (true) ili odliv (false) novca
}
