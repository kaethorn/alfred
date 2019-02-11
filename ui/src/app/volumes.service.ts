import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Publisher, Series, Volume } from './publisher';

@Injectable({
  providedIn: 'root'
})
export class VolumesService {
  constructor(private http: HttpClient) {}

  private readonly volumesBySeriesUrl = '/api/comics/search/findVolumesBySeries';
  private readonly volumesByPublisherUrl = '/api/comics/search/findVolumesBySeriesAndPublishers';
  private readonly markAsReadUrl = '/api/volumes/markAsRead';
  private readonly markAsUnreadUrl = '/api/volumes/markAsUnread';

  listVolumesBySeries(): Observable<Series[]> {
    return this.http.get(this.volumesBySeriesUrl).pipe(
      map((data: any) => data._embedded.publishers)
    );
  }

  listVolumesByPublisher (): Observable<Publisher[]> {
    return this.http.get(this.volumesByPublisherUrl).pipe(
      map((data: any) => data._embedded.publishers
        .map((publisher: Publisher) => {
          publisher.series
            .sort((a: Series, b: Series) => a.series.localeCompare(b.series))
            .map((series: Series) => series.volumes.sort((a: Volume, b: Volume) => a.volume.localeCompare(b.volume)));
          return publisher;
        })
      )
    );
  }

  markAsRead (volume: Volume): Observable<Volume> {
    return this.http.put<Volume>(`${ this.markAsReadUrl }`, volume);
  }

  markAsUnread (volume: Volume): Observable<Volume> {
    return this.http.put<Volume>(`${ this.markAsUnreadUrl }`, volume);
  }
}
