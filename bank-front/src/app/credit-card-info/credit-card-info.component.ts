import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { CreditCardInfoDTO } from 'src/dto/CreditCardInfoDTO';
import { CreditCardInfoService } from 'src/service/credit-card-info.service';

@Component({
  selector: 'app-credit-card-info',
  templateUrl: './credit-card-info.component.html',
  styleUrls: ['./credit-card-info.component.css']
})
export class CreditCardInfoComponent implements OnInit {

  constructor(private creditCardService: CreditCardInfoService) { }

  ngOnInit(): void {
  }

  creditCardInfoForm = new FormGroup({
    firstFourDigits: new FormControl(''),
    secondFourDigits: new FormControl(''),
    thirdFourDigits: new FormControl(''),
    lastFourDigits: new FormControl(''),
    cardholderName: new FormControl(''),
    expirationMonth: new FormControl(''),
    expirationYear: new FormControl(''),
    cvc: new FormControl('')
  })

  get firstFourFigits() { return this.creditCardInfoForm.get('firstFourDigits')}
  get secondFourDigits() { return this.creditCardInfoForm.get('secondFourDigits')}
  get thirdFourDigits() { return this.creditCardInfoForm.get('thirdFourDigits')}
  get lastFourDigits() { return this.creditCardInfoForm.get('lastFourDigits')}
  get cardholderName() { return this.creditCardInfoForm.get('cardholderName')}
  get expirationMonth() { return this.creditCardInfoForm.get('expirationMonth')}
  get expirationYear() { return this.creditCardInfoForm.get('expirationYear')}
  get cvc() { return this.creditCardInfoForm.get('cvc')}

  submit() {
    const firstFourDigits = this.firstFourFigits?.value;
    const secondFourDigits = this.secondFourDigits?.value;
    const thirdFourDigits = this.thirdFourDigits?.value;
    const lastFourDigits = this.lastFourDigits?.value;
    const cardholderName = this.cardholderName?.value;
    const expirationMonth = this.expirationMonth?.value;
    const expirationYear = this.expirationYear?.value;
    const cvv = this.cvc?.value;
    const pan = firstFourDigits + secondFourDigits + thirdFourDigits + lastFourDigits;
    const expirationDate = new Date(expirationYear, expirationMonth - 1);

    let creditCardInfoDto = new CreditCardInfoDTO({
      pan,
      cvv,
      cardholderName,
      expirationDate
    })

    this.creditCardService.send(creditCardInfoDto).subscribe((data: any) => {
      alert(data.merchantOrderId + " " + data.acquirerOrderId 
      + " " + data.acquirerTimestamp + " " + data.paymentId 
      + " " + data.successfullyCompleted + " " + data.successfullyCompleted 
      + " " + data.transactionAmount)
    }, (err) => {
      alert(err);
    })



  }

}
