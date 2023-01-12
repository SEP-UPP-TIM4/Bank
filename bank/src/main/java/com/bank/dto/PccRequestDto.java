package com.bank.dto;

import com.bank.model.Currency;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PccRequestDto {

    private Long acquirerOrderId;

    private LocalDateTime acquirerTimestamp;

    @Size(min = 5)
    private String pan;

    private String cardholderName;

    private String cvv;

    private LocalDate expirationDate;

    private BigDecimal amount;

    private Currency currency;
}
