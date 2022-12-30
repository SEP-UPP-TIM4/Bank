package com.bank.service;

import com.bank.dto.NewQrCodeDto;
import com.bank.dto.PaymentResponseDto;
import com.bank.exception.ErrorInCommunicationException;
import com.bank.exception.NotFoundException;
import com.bank.model.Account;
import com.bank.model.Payment;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class QrCodeService {

    private final PaymentService paymentService;

    private final AccountService accountService;
    private final RestTemplate restTemplate;


    private final String FINISH_PAYMENT_URL = "http://localhost:8081/QR-CODE-SERVICE/api/v1/payment/finish";

    public QrCodeService(PaymentService paymentService, AccountService accountService, RestTemplate restTemplate) {
        this.paymentService = paymentService;
        this.accountService = accountService;
        this.restTemplate = restTemplate;
    }

    public BufferedImage generateQrCode(Long paymentId) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        Payment payment = paymentService.findById(paymentId);
        NewQrCodeDto qrCodeDto = new NewQrCodeDto("PR", "01", "1", payment.getTransaction().getAcquirer().getNumber(),
                                                    payment.getTransaction().getAcquirer().getName(), payment.getTransaction().getCurrency().toString() + payment.getTransaction().getAmount().toString(),
                                                    "Katarina Žerajić, Nemanjića bb, Nevesinje", "221", "uplata na račun", "97");
        BitMatrix bitMatrix = qrCodeWriter.encode(generateQrCodeString(qrCodeDto), BarcodeFormat.QR_CODE, 250, 250, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }


    private String generateQrCodeString(NewQrCodeDto qrCodeDto){
        String qrCodeString = "";
        if(!qrCodeDto.getK().equals(""))
            qrCodeString += "K:" + qrCodeDto.getK();
        if(!qrCodeDto.getV().equals(""))
            qrCodeString += "|V:" + qrCodeDto.getV();
        if(!qrCodeDto.getC().equals(""))
            qrCodeString += "|C:" + qrCodeDto.getC();
        if(!qrCodeDto.getR().equals(""))
            qrCodeString += "|R:" + qrCodeDto.getR();
        if(!qrCodeDto.getN().equals(""))
            qrCodeString += "|N:" + qrCodeDto.getN();
        if(!qrCodeDto.getI().equals(""))
            qrCodeString += "|I:" + qrCodeDto.getI();
        if(!qrCodeDto.getP().equals(""))
            qrCodeString += "|P:" + qrCodeDto.getP();
        if(!qrCodeDto.getSf().equals(""))
            qrCodeString += "|SF:" + qrCodeDto.getSf();
        if(!qrCodeDto.getS().equals(""))
            qrCodeString += "|S:" + qrCodeDto.getS();
        if(!qrCodeDto.getRo().equals(""))
            qrCodeString += "|RO:" + qrCodeDto.getRo();

        return qrCodeString;
    }

    public Payment payByQrCode(Long paymentId, UUID issuerUuid){
        Payment payment = paymentService.findById(paymentId);
        Account account = accountService.getById(issuerUuid);
        processPayment(issuerUuid, payment);
        payment.getTransaction().setIssuer(account);
        return paymentService.save(payment);
    }

    private Account processPayment(UUID issuerUuid, Payment payment) {
        Optional<Account> account = Optional.ofNullable(accountService.getById(issuerUuid));
        if(account.isPresent()){
            return accountService.pay(account.get(), payment);
        } else {
            return callPcp(payment);
        }
    }

    private Account callPcp(Payment payment) {
        try {
            // TODO: make rest call to PCC to get account from other bank
            return makeRestCall(payment);
        } catch (Exception ex) {
            throw new ErrorInCommunicationException(payment.getErrorUrl());
        }
    }

    private Account makeRestCall(Payment payment) {
        return new Account();
    }

    public static String readQR(BufferedImage qrCode) throws FileNotFoundException, IOException, NotFoundException, com.google.zxing.NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(qrCode)));

        Result result
                = new MultiFormatReader().decode(binaryBitmap);

        return result.getText();
    }

    public void finishPayment(Payment payment) {
        PaymentResponseDto paymentDto = new PaymentResponseDto();
        paymentDto.setMerchantOrderId(payment.getMerchantOrderId());
        paymentDto.setAcquirerOrderId(payment.getTransaction().getId());
        paymentDto.setAcquirerTimestamp(payment.getTransaction().getTimeStamp());
        paymentDto.setPaymentId(payment.getId());
        paymentDto.setTransactionStatus(payment.getTransaction().getStatus().toString());
        paymentDto.setTransactionAmount(payment.getTransaction().getAmount());
        try {
            // TODO: try to get this url from database
            restTemplate.postForObject(FINISH_PAYMENT_URL, paymentDto, PaymentResponseDto.class);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }
}
