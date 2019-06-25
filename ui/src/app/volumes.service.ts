import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, GroupedObservable } from 'rxjs';
import { map, groupBy } from 'rxjs/operators';

import { Publisher } from './publisher';
import { Series } from './series';
import { Volume } from './volume';
import { Comic } from './comic';

@Injectable({
  providedIn: 'root'
})
export class VolumesService {

  constructor (
    private http: HttpClient
  ) { }

  private consumeHateoas (namespace: string): any {
    return map((data: any) => {
      return (data._embedded ? data._embedded[namespace] : []);
    });
  }

  listPublishers (): Observable<Publisher[]> {
    return this.http.get('/api/publishers').pipe(
      this.consumeHateoas('publishers')
    );
  }

  listPublishersWithSeries (): Observable<Publisher[]> {
    return this.http.get(`/api/publishers/series`).pipe(
      this.consumeHateoas('publishers')
    );
  }

  listSeries (publisher: string): Observable<Series[]> {
    return this.http.get(`/api/publishers/${ publisher }/series`).pipe(
      this.consumeHateoas('series')
    );
  }

  listVolumes (publisher: string, series: string): Observable<Volume[]> {
    return this.http.get(`/api/publishers/${ publisher }/series/${ series }/volumes`).pipe(
      this.consumeHateoas('volumes')
    );
  }

  markAsRead (volume: Volume): Observable<Volume> {
    return this.http.put<Volume>('/api/volumes/markAsRead', volume);
  }

  markAsUnread (volume: Volume): Observable<Volume> {
    return this.http.put<Volume>('/api/volumes/markAsUnread', volume);
  }

  markAllAsReadUntil (comic: Comic): Observable<any> {
    return this.http.put('api/volumes/markAllAsReadUntil', comic);
  }
}
