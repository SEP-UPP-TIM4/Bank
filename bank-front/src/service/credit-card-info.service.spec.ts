import { TestBed } from '@angular/core/testing';

import { CreditCardInfoService } from './credit-card-info.service';

describe('CreditCardInfoService', () => {
  let service: CreditCardInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CreditCardInfoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
