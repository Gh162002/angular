import { Injectable } from '@angular/core';
import {Eventy} from '../../models/eventy';
import {HttpClient} from '@angular/common/http';
import {catchError, map, of, tap} from 'rxjs';

const MOCK_EVENTS: Eventy[] = [
  {
    id: 1,
    title: 'Conférence UX Tunis',
    description: 'Une journée immersive dédiée aux dernières tendances UX/UI.',
    date: new Date(),
    location: 'Tunis',
    price: 120,
    organizerId: 101,
    imageUrl: '/images/event.png',
    nbPlaces: 120,
    nbrLike: 24
  },
  {
    id: 2,
    title: 'Tech Meetup Sfax',
    description: 'Rencontre des développeurs JS, partage et networking.',
    date: new Date(),
    location: 'Sfax',
    price: 0,
    organizerId: 205,
    imageUrl: '/images/ticket.png',
    nbPlaces: 80,
    nbrLike: 12
  }
];

@Injectable({
  providedIn: 'root'
})
export class EventsService {
  urlBackend = 'http://localhost:3000/events/';
  constructor(private http: HttpClient) { }
  public getAllEvents(){
       return this.http.get<Eventy[]>(this.urlBackend).pipe(
         catchError(() => of(MOCK_EVENTS))
       )
  }
  public getEventById(id:number){
      return this.http.get<Eventy>(this.urlBackend+id).pipe(
        catchError(() => of(MOCK_EVENTS.find(e => e.id === +id)!))
      )
  }
  addEvent(event:Eventy){
    return this.http.post<Eventy>(this.urlBackend, event).pipe(
      catchError(() => {
        MOCK_EVENTS.push(event);
        return of(event);
      })
    )
  }
  deleteEvent(id:number){
    return this.http.delete<Eventy>(this.urlBackend+id)
  }
  updateEvent(id:number, event:Eventy){
    return this.http.put<Eventy>(this.urlBackend+id, event)
  }
  searchByLocation(location:string){
    return this.http.get<Eventy[]>(this.urlBackend+'?location='+location)
  }
}
