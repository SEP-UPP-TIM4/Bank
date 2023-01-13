package com.bank.controller;

import com.bank.dto.QrCodeDto;
import com.bank.dto.RedirectDto;
import com.bank.model.Payment;
import com.bank.service.QrCodeService;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/qr")
public class QrCodePaymentController {

    private final QrCodeService qrCodeService;

    public QrCodePaymentController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @CrossOrigin(origins = "http://localhost:4201")
    @PostMapping("gen/{paymentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public String generateQr(@PathVariable UUID paymentId) throws WriterException, IOException, NotFoundException {
        BufferedImage qrCode = qrCodeService.generateQrCode(paymentId);
        qrCodeService.validateQrCode(qrCode);
        return toBase64(qrCode);
    }

    @CrossOrigin(origins = "http://localhost:4201")
    @PostMapping("pay/{paymentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public RedirectDto processPayment(@RequestBody QrCodeDto qrCode, @PathVariable UUID paymentId) throws WriterException, IOException, NotFoundException {
        Payment payment = qrCodeService.payByQrCode(paymentId, UUID.fromString(qrCode.getIssuerUuid()));
        qrCodeService.finishPayment(payment);
        log.info("Payment wit id {} successfully finished!", paymentId);
        return new RedirectDto(payment.getSuccessUrl());
    }

    private BufferedImage toBufferedImage(String base64) throws IOException {
        String imageString = base64.split(",")[1];

        BufferedImage image = null;
        byte[] imageByte;

        imageByte = Base64.decodeBase64(imageString);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        image = ImageIO.read(bis);
        bis.close();

        File outputfile = new File("image.png");
        ImageIO.write(image, "png", outputfile);
        return image;
    }

    private String toBase64(BufferedImage img) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", out);
        byte[] bytes = out.toByteArray();

        String base64bytes = Base64.encodeBase64String(bytes);
        return "data:image/png;base64," + base64bytes;
    }
}
