package com.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;                                   //payment id

    @Setter
    private Long merchantOrderId;

    @Setter
    private LocalDateTime merchantTimestamp;

    @Setter
    private String successUrl;

    @Setter
    private String failedUrl;

    @Setter
    private String errorUrl;

    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;

    @Setter
    @OneToOne
    private Transaction acquirerTransaction;

    @Setter
    @OneToOne
    private Transaction issuerTransaction;
}
