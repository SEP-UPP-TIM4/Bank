package com.bank.service;

import com.bank.dto.CreditCardInfoDto;
import com.bank.dto.PccRequestDto;
import com.bank.dto.PccResponseDto;
import com.bank.model.Payment;
import com.bank.model.PccResponse;
import com.bank.repository.PccResponseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PccResponseService {

    private final PccResponseRepository repository;

    private final RestTemplate restTemplate;

    private final ModelMapper modelMapper;

    public static final String PCC_URL = "http://" + System.getenv("ip_address") + ":9008/api/v1/pcc/forward";

    public PccResponseService(PccResponseRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.modelMapper = new ModelMapper();
    }

    public PccResponse makeRestCallToPcc(CreditCardInfoDto creditCardInfoDto, Payment payment) {
        PccRequestDto dto = PccRequestDto.builder()
                .acquirerOrderId(payment.getAcquirerTransaction().getId())
                .acquirerTimestamp(payment.getAcquirerTransaction().getTimestamp())
                .pan(creditCardInfoDto.getPan())
                .cardholderName(creditCardInfoDto.getCardholderName())
                .cvv(creditCardInfoDto.getCvv())
                .expirationDate(creditCardInfoDto.getExpirationDate())
                .amount(payment.getAcquirerTransaction().getAmount())
                .currency(payment.getAcquirerTransaction().getCurrency())
                .build();
        PccResponseDto responseDto = restTemplate.postForObject(PCC_URL, dto, PccResponseDto.class);
        return repository.save(modelMapper.map(responseDto, PccResponse.class));
    }
}
