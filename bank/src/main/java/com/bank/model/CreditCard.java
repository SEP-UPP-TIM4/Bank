package com.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    private String pan;

    @Setter
    private String cvv;

    @Setter
    private LocalDate expirationDate;   //mjesec i

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
