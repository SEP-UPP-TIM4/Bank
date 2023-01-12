package com.bank.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PccResponse {

    @Id
    @GeneratedValue
    private UUID id;

    @Setter
    private Long acquirerOrderId;

    @Setter
    private LocalDateTime acquirerTimestamp;

    @Setter
    private Long issuerOrderId;

    @Setter
    private LocalDateTime issuerTimestamp;

    @Setter
    private boolean success;
}
