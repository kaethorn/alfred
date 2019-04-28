import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Publisher, Volume } from './publisher';
import { Comic } from './comic';

@Injectable({
  providedIn: 'root'
})
export class VolumesService {
  constructor(private http: HttpClient) {}

  private readonly volumesByPublisherUrl = '/api/publishers';
  private readonly markAsReadUrl = '/api/volumes/markAsRead';
  private readonly markAsUnreadUrl = '/api/volumes/markAsUnread';
  private readonly markAllAsReadUntilUrl = 'api/volumes/markAllAsReadUntil';

  private consumeHateoas (): any {
    return map((data: any) => data._embedded.publishers);
  }

  listVolumesByPublisher (): Observable<Publisher[]> {
    return this.http.get(this.volumesByPublisherUrl).pipe(
      this.consumeHateoas()
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
