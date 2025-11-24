import {Component, OnDestroy, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';
import {FeedbackService} from '../../../shared/data/feedback.service';
import {Feedback} from '../../../models/feedback';

@Component({
  selector: 'app-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './form.component.html',
  styleUrl: './form.component.css'
})
export class FormComponent implements OnInit, OnDestroy {
  eventId: string | null = null;
  feedbacks: Feedback[] = [];
  currentUserId = 1;
  currentRate = 0;
  currentContent = '';
  isEditing = false;
  editingFeedbackId?: number | string;
  loading = false;
  readonly stars = [1, 2, 3, 4, 5];
  private subscriptions = new Subscription();

  constructor(private route: ActivatedRoute,
              private feedbackService: FeedbackService) {}

  ngOnInit(): void {
    this.subscriptions.add(
      this.route.paramMap.subscribe(params => {
        this.eventId = params.get('id');
        this.resetFormState();
        this.loadFeedbacks();
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  get hasFeedbacks(): boolean {
    return this.feedbacks.length > 0;
  }

  setRate(rate: number): void {
    this.currentRate = rate;
  }

  startEdit(feedback: Feedback): void {
    this.isEditing = true;
    this.editingFeedbackId = feedback.id;
    this.currentRate = feedback.rate || 0;
    this.currentContent = feedback.content?.toString() || '';
  }

  cancelEdit(): void {
    this.resetFormState();
  }

  onSubmit(): void {
    if (!this.eventId) {
      return;
    }

    const payload: Feedback = {
      id: this.editingFeedbackId || `fb-${Date.now()}`,
      id_event: Number(this.eventId),
      id_user: this.currentUserId,
      content: this.currentContent.trim(),
      rate: this.currentRate,
      date: new Date().toISOString()
    };

    this.loading = true;
    const request$ = this.isEditing
      ? this.feedbackService.updateFeedback(payload)
      : this.feedbackService.createFeedback(payload);

    this.subscriptions.add(
      request$.subscribe({
        next: () => {
          this.resetFormState();
          this.loadFeedbacks();
        },
        complete: () => (this.loading = false),
        error: () => (this.loading = false)
      })
    );
  }

  deleteFeedback(id: number | string | undefined): void {
    if (!id) {
      return;
    }
    this.loading = true;
    this.subscriptions.add(
      this.feedbackService.deleteFeedback(id).subscribe({
        next: () => this.loadFeedbacks(),
        complete: () => (this.loading = false),
        error: () => (this.loading = false)
      })
    );
  }

  trackByFeedback = (_: number, feedback: Feedback) => feedback.id;

  private loadFeedbacks(): void {
    if (!this.eventId) {
      this.feedbacks = [];
      return;
    }
    this.loading = true;
    this.subscriptions.add(
      this.feedbackService.getFeedbacks().subscribe({
        next: (data) => {
          this.feedbacks = data
            .filter(fb => String(fb.id_event) === String(this.eventId))
            .sort((a, b) => {
              const dateA = a.date ? new Date(a.date).getTime() : 0;
              const dateB = b.date ? new Date(b.date).getTime() : 0;
              return dateB - dateA;
            });
        },
        complete: () => (this.loading = false),
        error: () => (this.loading = false)
      })
    );
  }

  private resetFormState(): void {
    this.isEditing = false;
    this.editingFeedbackId = undefined;
    this.currentRate = 0;
    this.currentContent = '';
  }

}
