import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CreditCardInfoDTO } from 'src/app/dto/CreditCardInfoDTO';
import { config } from "src/shared"

@Injectable({
  providedIn: 'root'
})
export class CreditCardInfoService {

  private url = "api/v1/payment/credit-card"

  constructor(private http: HttpClient) { }

  send(creditCardInfoDto: CreditCardInfoDTO, paymentId: any ) {
    return this.http.post(`${config.baseUrl}${this.url}/${paymentId}`, creditCardInfoDto)
  }
}
