export class CreditCardInfoDTO {
    pan: string
    cvv: string
    cardholderName: string
    expirationDate: Date

    constructor({ pan, cvv, cardholderName, expirationDate }: any) {
        this.pan = pan;
        this.cvv = cvv;
        this.cardholderName = cardholderName;
        this.expirationDate = expirationDate;
      }
}
