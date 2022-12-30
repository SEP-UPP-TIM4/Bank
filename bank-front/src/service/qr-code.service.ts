import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { config } from "src/shared"

@Injectable({
  providedIn: 'root'
})
export class QrCodeService {
  
  private apiUrl = "api/v1/qr/"
  private generateQrUrl = "gen/"
  private payUrl = "pay/"

  constructor(private http: HttpClient) { }

  generateQr(paymentId: any ) {
    const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');
    return this.http.post(`${config.baseUrl}${this.apiUrl}${this.generateQrUrl}${paymentId}`, null,  { headers, responseType: 'text'});
  }

  pay(paymentId: any, qrCode: string) {
    return this.http.post(`${config.baseUrl}${this.apiUrl}${this.payUrl}${paymentId}`, {"qrCode": qrCode, "issuerUuid": "8e3416d8-6b2b-4ef4-acc0-489b8a57a745"});
  }
}
