import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { CreditCardInfoDTO } from 'src/app/dto/CreditCardInfoDTO';
import { CreditCardInfoService } from 'src/service/credit-card-info.service';
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'app-credit-card-info',
  templateUrl: './credit-card-info.component.html',
  styleUrls: ['./credit-card-info.component.css']
})
export class CreditCardInfoComponent implements OnInit {

  constructor(private creditCardService: CreditCardInfoService, private route: ActivatedRoute) { }

  ngOnInit(): void {
  }

  error = ""

  creditCardInfoForm = new FormGroup({
    creditCardNumber: new FormControl(''),
    cardholderName: new FormControl(''),
    expirationMonth: new FormControl(''),
    expirationYear: new FormControl(''),
    cvc: new FormControl('')
  })

  get creditCardNumber() { return this.creditCardInfoForm.get('creditCardNumber') }
  set creditCardNumber(text: any) { this.creditCardInfoForm.get('creditCardNumber')?.setValue(text) }
  get cardholderName() { return this.creditCardInfoForm.get('cardholderName') }
  get expirationMonth() { return this.creditCardInfoForm.get('expirationMonth') }
  get expirationYear() { return this.creditCardInfoForm.get('expirationYear') }
  get cvc() { return this.creditCardInfoForm.get('cvc') }

submit() {
  const credditCardNumber = this.creditCardNumber?.value;
  const cardholderName = this.cardholderName?.value;
  const expirationMonth = this.expirationMonth?.value;
  const expirationYear = this.expirationYear?.value;
  const cvv = this.cvc?.value;
  const pan = credditCardNumber;
  const expirationDate = new Date(expirationYear, expirationMonth, 1);

  let creditCardInfoDto = new CreditCardInfoDTO({
    pan,
    cvv,
    cardholderName,
    expirationDate
  })

  const paymentId = this.route.snapshot.paramMap.get('id');
  this.creditCardService.send(creditCardInfoDto, paymentId).subscribe((data: any) => {
    window.location.href = data.redirect
  }, (err: any) => {
    alert(err.error.redirect)
  })

}

onInput() {
  let number = this.cardholderName?.value
  if(number.length % 4 == 0 && number.length != 32 && number.length != 0) {
    this.creditCardNumber?.setValue(this.creditCardNumber?.value + "    ")
  }
}

}
