import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Comic } from './comic';

@Injectable({
  providedIn: 'root'
})
export class ComicsService {
  constructor(private http: HttpClient) {}

  private readonly scanUrl = 'api/scan';
  private readonly comicsUrl = 'api/comics/search/findAllByOrderBySeriesAscVolumeAscPositionAsc';
  private readonly lastUnreadsUrl = 'api/comics/search/findAllLastReadPerVolume';
  private readonly lastUnreadUrl = 'api/comics/search/findLastReadForVolume';
  private readonly comicsByVolumeUrl = 'api/comics/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition';
  private readonly firstByVolumeUrl = 'api/comics/search/findFirstByPublisherAndSeriesAndVolumeOrderByPosition';
  private readonly comicUrl = 'api/comics';

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

  getLastUnreadByVolume(publisher: string, series: string, volume: string): Observable<Comic> {
    const params = new HttpParams({
      fromObject: {
        publisher: publisher,
        series: series,
        volume: volume
      }
    });
    return this.http.get<Comic>(this.lastUnreadUrl, { params: params }).pipe(
      map((comic: any) => {
        comic.id = comic._links.self.href.split('/').pop();
        return comic;
      })
    );
  }

  getFirstByVolume(publisher: string, series: string, volume: string): Observable<Comic> {
    const params = new HttpParams({
      fromObject: {
        publisher: publisher,
        series: series,
        volume: volume
      }
    });
    return this.http.get<Comic>(this.firstByVolumeUrl, { params: params }).pipe(
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
    return this.http.get(this.lastUnreadsUrl).pipe(
      map((data: any) => data._embedded.comics),
      map((data: any) => {
        return data.map((comic) => {
          comic.id = comic._links.self.href.split('/').pop();
          return comic;
        });
      })
    );
  }

  update (comic: Comic): Observable<Comic> {
    return this.http.put<Comic>(`${ this.comicUrl }/${ comic.id }`, comic);
  }
}
