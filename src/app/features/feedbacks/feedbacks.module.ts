import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule, Routes} from '@angular/router';

import { FeedbacksComponent } from './feedbacks.component';
import {FormComponent} from './form/form.component';

const routes: Routes = [
  {
    path: '',
    component: FeedbacksComponent,
    children: [
      { path: 'event/:id', component: FormComponent }
    ]
  }
];

@NgModule({
  declarations: [
    FeedbacksComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes)
  ]
})
export class FeedbacksModule { }
