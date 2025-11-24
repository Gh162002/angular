import {Component, OnInit} from '@angular/core';
import {combineLatest, of} from 'rxjs';
import {catchError, finalize} from 'rxjs/operators';
import {EventsService} from '../../shared/data/events.service';
import {FeedbackService} from '../../shared/data/feedback.service';
import {Eventy} from '../../models/eventy';
import {Feedback} from '../../models/feedback';

interface EventAnalytics {
  id: number | string;
  title: string;
  date: string | Date;
  location: string;
  imageUrl: string;
  feedbackCount: number;
  averageRate: number;
  lastFeedbackAt?: Date | null;
}

interface FeedbackTimeline {
  feedback: Feedback;
  event?: Eventy;
}

@Component({
  selector: 'app-feedbacks',
  templateUrl: './feedbacks.component.html',
  styleUrl: './feedbacks.component.css'
})
export class FeedbacksComponent implements OnInit {
  loading = true;
  errorMessage = '';
  totalFeedbacks = 0;
  averageRate = 0;
  eventAnalytics: EventAnalytics[] = [];
  timeline: FeedbackTimeline[] = [];

  constructor(private eventsService: EventsService,
              private feedbackService: FeedbackService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  trackByEvent = (_: number, item: EventAnalytics) => item.id;
  trackByFeedback = (_: number, item: FeedbackTimeline) => item.feedback.id;

  private loadDashboard(): void {
    combineLatest([
      this.eventsService.getAllEvents(),
      this.feedbackService.getFeedbacks()
    ]).pipe(
      catchError(() => {
        this.errorMessage = 'Impossible de charger les données. Vérifiez le serveur JSON.';
        return of<[Eventy[], Feedback[]]>([[], []]);
      }),
      finalize(() => this.loading = false)
    ).subscribe(([events, feedbacks]) => {
      this.totalFeedbacks = feedbacks.length;
      this.averageRate = feedbacks.length
        ? Number(
            (feedbacks.reduce((sum, f) => sum + (f.rate || 0), 0) / feedbacks.length).toFixed(1)
          )
        : 0;

      const eventsMap = new Map(events.map(ev => [String(ev.id), ev]));

      this.eventAnalytics = events.map(ev => {
        const eventFeedbacks = feedbacks.filter(f => String(f.id_event) === String(ev.id));
        const avgRate = eventFeedbacks.length
          ? Number(
              (
                eventFeedbacks.reduce((sum, f) => sum + (f.rate || 0), 0) / eventFeedbacks.length
              ).toFixed(1)
            )
          : 0;

        const lastFeedback = eventFeedbacks
          .map(f => (f.date ? new Date(f.date) : null))
          .filter((date): date is Date => !!date)
          .sort((a, b) => b.getTime() - a.getTime())[0] || null;

        return {
          id: ev.id,
          title: ev.title,
          date: ev.date,
          location: ev.location,
          imageUrl: ev.imageUrl,
          feedbackCount: eventFeedbacks.length,
          averageRate: avgRate,
          lastFeedbackAt: lastFeedback
        };
      });

      this.timeline = feedbacks
        .map(feedback => ({
          feedback,
          event: eventsMap.get(String(feedback.id_event))
        }))
        .sort((a, b) => {
          const dateA = a.feedback.date ? new Date(a.feedback.date).getTime() : 0;
          const dateB = b.feedback.date ? new Date(b.feedback.date).getTime() : 0;
          return dateB - dateA;
        });
    });
  }
}
