package com.bank.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Setter
    private String successUrl;

    @Setter
    private String failedUrl;

    @Setter
    private String errorUrl;

    @OneToOne
    private Transaction transaction;
}
