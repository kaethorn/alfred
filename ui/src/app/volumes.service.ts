import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Publisher, Series, Volume } from './publisher';
import { Comic } from './comic';

@Injectable({
  providedIn: 'root'
})
export class VolumesService {
  constructor(private http: HttpClient) {}

  private readonly volumesByPublisherUrl = '/api/publishers/search/findAll';
  private readonly markAsReadUrl = '/api/volumes/markAsRead';
  private readonly markAsUnreadUrl = '/api/volumes/markAsUnread';
  private readonly markAllAsReadUntilUrl = 'api/volumes/markAllAsReadUntil';

  private sortSeriesAndVolumes (): any {
    return map((publishers: Publisher[]) => {
      return publishers.map((publisher: Publisher) => {
        publisher.series
          .sort((a: Series, b: Series) => a.series.localeCompare(b.series))
          .map((series: Series) => series.volumes.sort((a: Volume, b: Volume) => a.volume.localeCompare(b.volume)));
        return publisher;
      });
    });
  }

  listVolumesByPublisher (): Observable<Publisher[]> {
    return this.http.get(this.volumesByPublisherUrl).pipe(
      this.sortSeriesAndVolumes()
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
