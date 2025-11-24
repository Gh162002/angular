import { Component } from '@angular/core';
import {NgForm} from '@angular/forms';
import {Router} from '@angular/router';
import {EventsService} from '../../../shared/data/events.service';
import {Eventy} from '../../../models/eventy';

@Component({
  selector: 'app-form-event',
  templateUrl: './form-event.component.html',
  styleUrl: './form-event.component.css'
})
export class FormEventComponent {
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  event: Eventy = {
    id: Date.now(),
    title: '',
    description: '',
    date: new Date(),
    location: '',
    price: 0,
    organizerId: 0,
    imageUrl: '',
    nbPlaces: 0,
    nbrLike: 0
  };

  constructor(private eventsService: EventsService,
              private router: Router) {}

  onSubmit(form: NgForm) {
    this.successMessage = '';
    this.errorMessage = '';

    if (form.invalid) {
      form.control.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    const payload: Eventy = {
      ...this.event,
      id: Date.now()
    };

    this.eventsService.addEvent(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMessage = 'Événement ajouté avec succès !';
        form.resetForm();
        this.event = {
          id: Date.now(),
          title: '',
          description: '',
          date: new Date(),
          location: '',
          price: 0,
          organizerId: 0,
          imageUrl: '',
          nbPlaces: 0,
          nbrLike: 0
        };
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMessage = 'Impossible de sauvegarder l’événement. Vérifiez le serveur JSON.';
      }
    });
  }
}
