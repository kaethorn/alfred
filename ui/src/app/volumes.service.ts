import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Publisher } from './publisher';
import { Series } from './series';
import { Volume } from './volume';
import { Comic } from './comic';

@Injectable({
  providedIn: 'root'
})
export class VolumesService {
  constructor (private http: HttpClient) {}

  private readonly publishersUrl = '/api/publishers';
  private readonly markAsReadUrl = '/api/volumes/markAsRead';
  private readonly markAsUnreadUrl = '/api/volumes/markAsUnread';
  private readonly markAllAsReadUntilUrl = 'api/volumes/markAllAsReadUntil';

  private consumeHateoas (namespace: string): any {
    return map((data: any) => {
      return (data._embedded ? data._embedded[namespace] : []);
    });
  }

  listPublishers (): Observable<Publisher[]> {
    return this.http.get(this.publishersUrl).pipe(
      this.consumeHateoas('publishers')
    );
  }

  listSeries (publisher: string): Observable<Series[]> {
    return this.http.get(`${ this.publishersUrl }/${ publisher }/series`).pipe(
      this.consumeHateoas('series')
    );
  }

  listVolumes (publisher: string, series: string): Observable<Volume[]> {
    return this.http.get(`${ this.publishersUrl }/${ publisher }/series/${ series }/volumes`).pipe(
      this.consumeHateoas('volumes')
    );
  }

  markAsRead (volume: Volume): Observable<Volume> {
    return this.http.put<Volume>(`${ this.markAsReadUrl }`, volume);
  }

  markAsUnread (volume: Volume): Observable<Volume> {
    return this.http.put<Volume>(`${ this.markAsUnreadUrl }`, volume);
  }

  markAllAsReadUntil (comic: Comic): Observable<any> {
    return this.http.put(this.markAllAsReadUntilUrl, comic);
  }
}
