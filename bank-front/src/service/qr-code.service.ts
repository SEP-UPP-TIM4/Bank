import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { config } from "src/shared"

@Injectable({
  providedIn: 'root'
})
export class QrCodeService {
  
  private apiUrl = "api/v1/qr/"
  private generateQrUrl = "gen/"

  constructor(private http: HttpClient) { }

  generateQr(paymentId: any ) {
    const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');
    return this.http.post(`${config.baseUrl}${this.apiUrl}${this.generateQrUrl}${paymentId}`, null,  { headers, responseType: 'text'});
  }
}
