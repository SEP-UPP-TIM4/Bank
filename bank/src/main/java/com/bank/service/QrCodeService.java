package com.bank.service;

import com.bank.dto.CreditCardInfoDto;
import com.bank.dto.NewQrCodeDto;
import com.bank.exception.NotFoundException;
import com.bank.exception.QrCodeNotValidException;
import com.bank.model.Account;
import com.bank.model.CreditCard;
import com.bank.model.Payment;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class QrCodeService {

    private final PaymentService paymentService;

    private final AccountService accountService;

    private final CreditCardService creditCardService;

    private final TripleDes tripleDes;

    public QrCodeService(PaymentService paymentService, AccountService accountService, CreditCardService creditCardService, TripleDes tripleDes) {
        this.paymentService = paymentService;
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.tripleDes = tripleDes;
    }

    public BufferedImage generateQrCode(UUID paymentId) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        Payment payment = paymentService.findById(paymentId);
        NewQrCodeDto qrCodeDto = new NewQrCodeDto("PR", "01", "1", payment.getAcquirerTransaction().getAccount().getNumber(),
                                                    payment.getAcquirerTransaction().getAccount().getName(), payment.getAcquirerTransaction().getCurrency().toString() + payment.getAcquirerTransaction().getAmount().toString(),
                                                    "Katarina Žerajić, Nemanjića bb, Nevesinje", "221", "uplata na račun", "");
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

    public Payment payByQrCode(UUID paymentId, UUID issuerUuid){
        Payment payment = paymentService.findById(paymentId);
        Account account = accountService.getById(issuerUuid);
        CreditCard creditCard = creditCardService.findByAccountId(account.getId());
        CreditCardInfoDto creditCardInfoDto = CreditCardInfoDto.builder()
                .pan(tripleDes.decrypt(creditCard.getPan())).cvv(creditCard.getCvv()).cardholderName(account.getName())
                .expirationDate(creditCard.getExpirationDate()).build();
        return paymentService.processPayment(creditCardInfoDto, payment);
    }

    public void finishPayment(Payment payment, String finishUrl) {
        paymentService.finishPayment(payment, finishUrl);
    }

    public static String readQR(BufferedImage qrCode) throws NotFoundException, com.google.zxing.NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(qrCode)));

        Result result
                = new MultiFormatReader().decode(binaryBitmap);

        return result.getText();
    }

    public void validateQrCode(BufferedImage qrCode) throws com.google.zxing.NotFoundException, IOException {
        String qrCodeText = readQR(qrCode);
        String[] qrParts = qrCodeText.split("\\|");
        if(qrCodeText.charAt(0) == '|') throw new QrCodeNotValidException("'|' should not be at the beginning.");
        if(qrCodeText.charAt(qrCodeText.length() - 1) == '|') throw new QrCodeNotValidException("'|' should not be at the end.");
        if(!isKValid(qrParts[0])) throw new QrCodeNotValidException("Identification code (K) not valid");
        if(!isVValid(qrParts[1])) throw new QrCodeNotValidException("Version (V) not valid");
        if(!isCValid(qrParts[2])) throw new QrCodeNotValidException("Character set (C) not valid");
        if(!isRValid(qrParts[3])) throw new QrCodeNotValidException("Account number (R) not valid");
        if(!isNValid(qrParts[4])) throw new QrCodeNotValidException("Payee personal data (N) not valid");
        if(!isIValid(qrParts[5])) throw new QrCodeNotValidException("Payment amount (I) not valid");
        if(!isPValid(qrParts[6])) throw new QrCodeNotValidException("Payer personal data (P) not valid");
        if(!isSfValid(qrParts[7])) throw new QrCodeNotValidException("Payment code (SF) not valid");
        if(!isSValid(qrParts[8])) throw new QrCodeNotValidException("Payment purpose (S) not valid");
        if(qrParts.length == 10) {
            if (!isROValid(qrParts[9])) throw new QrCodeNotValidException("Model and reference number (RO) not valid");
        }
    }

    public boolean isKValid(String K) {
        if(!K.split(":")[0].equals("K")) return false;
        return K.split(":")[1].equals("PR");
    }
    public boolean isVValid(String V) {
        if(!V.split(":")[0].equals("V")) return false;
        return V.split(":")[1].equals("01");
    }

    public boolean isCValid(String C) {
        if(!C.split(":")[0].equals("C")) return false;
        return C.split(":")[1].equals("1");
    }

    public boolean isRValid(String R) {
        if(!R.split(":")[0].equals("R")) return false;
        String s = R.split(":")[1];
        if(s.length() != 18)
            return false;
        for(char c : s.toCharArray()){
            if(!Character.isDigit(c)){
                return false;
            }
        }
        return true;
    }

    public boolean isNValid(String N) {
        if(!N.split(":")[0].equals("N")) return false;
        String s = N.split(":")[1];
        if(s.split("\r\n|\r|\n").length > 3)
            return false;
        s.replace("\n", "").replace("\r", "");
        if(s.length() > 70)
            return false;

        return true;
    }

    public boolean isIValid(String I) {
        if(!I.split(":")[0].equals("I")) return false;
        Pattern p = Pattern.compile("[A-Z]{3}[0-9]+.[0-9]*{5,18}");
        String s = I.split(":")[1];
        Matcher m = p.matcher(s);
        if(m.matches())
            return true;
        return false;
    }

    public boolean isPValid(String P) {
        String s = P.split(":")[1];
        if(s.split("\r\n|\r|\n").length > 3)
            return false;
        s.replace("\n", "").replace("\r", "");
        if(s.length() > 70)
            return false;

        return true;
    }

    public boolean isSfValid(String sF) {
        if(!sF.split(":")[0].equals("SF")) return false;
        String s = sF.split(":")[1];
        Pattern p = Pattern.compile("[1|2]{1}[0-9]{2}");
        Matcher m = p.matcher(s);
        if(m.matches())
            return true;
        return false;
    }

    public boolean isSValid(String S) {
        String s = S.split(":")[1];
        if(s.equals(""))
            return true;
        if(s.split("\r\n|\r|\n").length > 1)
            return false;
        if(s.length() > 35)
            return false;
        return true;
    }

    public boolean isROValid(String ro) {
        String s = ro.split(":")[1];
        if(s.equals(""))
            return true;
        if(s.length() > 25)
            return false;
        Pattern p = Pattern.compile("[0-9]{2}[0-9|-]+");
        Matcher m = p.matcher(s);
        if(s.contains("-") && s.charAt(0) == '9' && s.charAt(1) == '7')
            return false;
        if(m.matches())
            return true;
        return false;
    }
}
