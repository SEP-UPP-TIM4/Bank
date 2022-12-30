package com.bank.service;

import com.bank.dto.NewQrCodeDto;
import com.bank.model.Payment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

@Service
public class QrCodeService {

    private final PaymentService paymentService;

    public QrCodeService(PaymentService paymentService) {
        this.paymentService = paymentService;
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
}
