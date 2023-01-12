package com.bank.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentResponseDto {

    private Long merchantOrderId;

    private Long acquirerOrderId;

    private LocalDateTime acquirerTimestamp;

    private Long paymentId;

    private String transactionStatus;

    private BigDecimal transactionAmount;

}
