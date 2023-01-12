package com.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PccResponseDto {

    private Long acquirerOrderId;

    private LocalDateTime acquirerTimestamp;

    private Long issuerOrderId;

    private LocalDateTime issuerTimestamp;

    private boolean success;
}
