import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { FeedbacksRoutingModule } from './feedbacks-routing.module';
import { FeedbacksComponent } from './feedbacks.component';
import { FormComponent } from './form/form.component';


@NgModule({
  declarations: [
    FeedbacksComponent,
    FormComponent
  ],
  imports: [
    CommonModule,
    RouterModule,
    FeedbacksRoutingModule
  ]
})
export class FeedbacksModule { }
