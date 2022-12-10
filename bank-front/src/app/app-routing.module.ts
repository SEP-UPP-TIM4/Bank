import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreditCardInfoComponent } from './credit-card-info/credit-card-info.component';

const routes: Routes = [
  { path: ':id', component: CreditCardInfoComponent},
  { path: 'redirect/:id', component: CreditCardInfoComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
