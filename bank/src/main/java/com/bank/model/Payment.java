package com.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                    //payment id

    private Long merchantOrderId;

    private LocalDateTime merchantTimestamp;

    @Setter
    private String successUrl;

    @Setter
    private String failedUrl;

    @Setter
    private String errorUrl;

    @Setter
    @OneToOne
    private Transaction acquirerTransaction;

    @Setter
    @OneToOne
    private Transaction issuerTransaction;
}
