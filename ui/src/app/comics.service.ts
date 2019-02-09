import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Comic } from './comic';
import { Publisher, Series, Volume } from './publisher';

@Injectable({
  providedIn: 'root'
})
export class ComicsService {
  constructor(private http: HttpClient) {}

  private API_PREFIX: String = 'api';

  private scanUrl =
    `${ this.API_PREFIX }/scan`;
  private comicsUrl =
    `${ this.API_PREFIX }/comics/search/findAllByOrderBySeriesAscVolumeAscPositionAsc`;
  private lastUnreadUrl =
    `${ this.API_PREFIX }/comics/search/findAllLastReadByVolume`;
  private comicsByVolumeUrl =
    `${ this.API_PREFIX }/comics/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition`;
  private comicUrl =
    `${ this.API_PREFIX }/comics`;
  private volumesBySeriesUrl =
    `${ this.API_PREFIX }/comics/search/findVolumesBySeries`;
  private volumesByPublisherUrl =
    `${ this.API_PREFIX }/comics/search/findVolumesBySeriesAndPublishers`;

  list (): Observable<Comic[]> {
    return this.http.get(this.comicsUrl).pipe(
      map((data: any) => data._embedded.comics),
      map((data: any) => {
        return data.map((comic) => {
          comic.id = comic._links.self.href.split('/').pop();
          return comic;
        });
      })
    );
  }

  listByVolume (publisher: string, series: string, volume: string): Observable<Comic[]> {
    const params = new HttpParams({
      fromObject: {
        publisher: publisher,
        series: series,
        volume: volume
      }
    });
    return this.http.get(this.comicsByVolumeUrl, { params: params }).pipe(
      map((data: any) => data._embedded.comics),
      map((data: any) => {
        return data.map((comic) => {
          comic.id = comic._links.self.href.split('/').pop();
          return comic;
        });
      })
    );
  }

  get (id: string): Observable<Comic> {
    return this.http.get<Comic>(`${ this.comicUrl }/${ id }`).pipe(
      map((comic: any) => {
        comic.id = comic._links.self.href.split('/').pop();
        return comic;
      })
    );
  }

  scan () {
    return this.http.get(this.scanUrl);
  }

  listLastReadByVolume(): Observable<Comic[]> {
    return this.http.get(this.lastUnreadUrl).pipe(
      map((data: any) => data._embedded.comics),
      map((data: any) => {
        return data.map((comic) => {
          comic.id = comic._links.self.href.split('/').pop();
          return comic;
        });
      })
    );
  }

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

  update (comic: Comic) {
    return this.http.put<Comic>(`${ this.comicUrl }/${ comic.id }`, comic);
  }
}
