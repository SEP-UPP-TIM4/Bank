package com.bank.mapper;

import com.bank.dto.PaymentResponseDto;
import com.bank.model.Payment;

public class PaymentMapper{

    public static PaymentResponseDto PaymentToPaymentResponseDto(Payment payment){

        return new PaymentResponseDto(
                payment.getMerchantOrderId(), payment.getTransaction().getId(), payment.getTransaction().getTimeStamp(),
                payment.getId(), payment.getTransaction().getStatus().toString(), payment.getTransaction().getAmount());
    }

}
