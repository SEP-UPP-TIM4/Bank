package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ValidateRequestDto {

    private UUID merchantId;

    private String merchantPassword;

    private BigDecimal amount;

    private String currency;

    private Long merchantOrderId;

    private LocalDateTime merchantTimestamp;

    private String successUrl;

    private String failedUrl;

    private String errorUrl;
}
