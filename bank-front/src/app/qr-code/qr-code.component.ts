import { Component, OnInit } from '@angular/core';
import { QrCodeService } from 'src/service/qr-code.service';
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'app-qr-code',
  templateUrl: './qr-code.component.html',
  styleUrls: ['./qr-code.component.css']
})
export class QrCodeComponent implements OnInit {

  qrCode: any;

  constructor(private qrCodeService: QrCodeService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    const paymentId = this.route.snapshot.paramMap.get('id');
    this.qrCodeService.generateQr(paymentId).subscribe((data: any) => {
      this.qrCode = data;
    }, (err: any) => {
      alert("An error occured, please try again...");
    })
  }

}
