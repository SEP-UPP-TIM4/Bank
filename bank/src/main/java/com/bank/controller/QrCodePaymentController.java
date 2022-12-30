package com.bank.controller;

import com.bank.service.QrCodeService;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    public String generateQr(@PathVariable Long paymentId) throws WriterException, IOException {
        return toBase64(qrCodeService.generateQrCode(paymentId));
    }

    private String toBase64(BufferedImage img) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", out);
        byte[] bytes = out.toByteArray();

        String base64bytes = Base64.encodeBase64String(bytes);
        return "data:image/png;base64," + base64bytes;
    }
}
