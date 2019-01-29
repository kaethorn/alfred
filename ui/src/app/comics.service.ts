import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Comic } from './comic';
import { Publisher, Series } from './publisher';

@Injectable({
  providedIn: 'root'
})
export class ComicsService {
  constructor(private http: HttpClient) {}

  private API_PREFIX: String = 'api';

  private scanUrl: string =
    `${ this.API_PREFIX }/scan`;
  private comicsUrl: string =
    `${ this.API_PREFIX }/comics/search/findAllByOrderBySeriesAscVolumeAscPositionAsc`;
  private comicsByVolumeUrl: string =
    `${ this.API_PREFIX }/comics/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition`;
  private comicUrl: string =
    `${ this.API_PREFIX }/comics`;
  private volumesBySeriesUrl: string =
    `${ this.API_PREFIX }/comics/search/findVolumesBySeries`;
  private volumesByPublisherUrl: string =
    `${ this.API_PREFIX }/comics/search/findVolumesBySeriesAndPublishers`;

  list () : Observable<Comic[]> {
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

  listByVolume (publisher: string, series: string, volume: string) : Observable<Comic[]> {
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

  get (id: string) : Observable<Comic> {
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

  listVolumesBySeries() : Observable<Series[]> {
    return this.http.get(this.volumesBySeriesUrl).pipe(
      map((data: any) => data._embedded.publishers)
    );
  }

  listVolumesByPublisher () : Observable<Publisher[]> {
    return this.http.get(this.volumesByPublisherUrl).pipe(
      map((data: any) => data._embedded.publishers)
    );
  }
}
