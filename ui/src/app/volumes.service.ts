import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Comic } from './comic';
import { Publisher } from './publisher';
import { Series } from './series';
import { Volume } from './volume';

@Injectable({
  providedIn: 'root'
})
export class VolumesService {

  constructor(
    private http: HttpClient
  ) { }

  public listPublishers(): Observable<Publisher[]> {
    return this.http.get('/api/publishers').pipe(
      this.consumeHateoas('publishers')
    );
  }

  public listSeries(publisher: string): Observable<Series[]> {
    return this.http.get(`/api/publishers/${ publisher }/series`).pipe(
      this.consumeHateoas('series')
    );
  }

  public listVolumes(publisher: string, series: string): Observable<Volume[]> {
    return this.http.get(`/api/publishers/${ publisher }/series/${ series }/volumes`).pipe(
      this.consumeHateoas('volumes')
    );
  }

  public markAsRead(volume: Volume): Observable<void> {
    return this.http.put<void>('/api/volumes/markAsRead', volume);
  }

  public markAsUnread(volume: Volume): Observable<void> {
    return this.http.put<void>('/api/volumes/markAsUnread', volume);
  }

  public markAllAsReadUntil(comic: Comic): Observable<void> {
    return this.http.put<void>('/api/volumes/markAllAsReadUntil', comic);
  }

  private consumeHateoas(namespace: string): any {
    return map((data: any) =>
      (data._embedded ? data._embedded[namespace] : [])
    );
  }
}
