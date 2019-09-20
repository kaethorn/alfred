import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Comic } from './comic';

@Injectable({
  providedIn: 'root'
})
export class ComicsService {

  constructor (
    private http: HttpClient
  ) {}

  private consumeHateoas (): any {
    return map((data: any) => {
      return (data._embedded ? data._embedded.comics : []);
    });
  }

  private addId (item: any): any {
    item.id = item._links.self.href.split('/').pop();
    return item;
  }

  list (): Observable<Comic[]> {
    return this.http.get('api/comics/search/findAllByOrderBySeriesAscVolumeAscPositionAsc').pipe(
      this.consumeHateoas(),
      map((data: any) => data.map((comic) => this.addId(comic)))
    );
  }

  listComicsWithErrors (): Observable<Comic[]> {
    return this.http.get('api/queue').pipe(
      this.consumeHateoas(),
      map((data: any) => data.map((comic) => this.addId(comic)))
    );
  }

  listByVolume (publisher: string, series: string, volume: string): Observable<Comic[]> {
    const params = new HttpParams({
      fromObject: {
        publisher,
        series,
        volume
      }
    });
    return this.http.get('api/comics/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition', { params }).pipe(
      this.consumeHateoas(),
      map((data: any) => data.map((comic) => this.addId(comic)))
    );
  }

  get (id: string): Observable<Comic> {
    return this.http.get<Comic>(`api/comics/${ id }`).pipe(
      map((comic: any) => this.addId(comic))
    );
  }

  getLastUnreadByVolume (publisher: string, series: string, volume: string): Observable<Comic> {
    const params = new HttpParams({
      fromObject: {
        publisher,
        series,
        volume
      }
    });
    return this.http.get<Comic>('api/comics/search/findLastReadForVolume', { params }).pipe(
      map((comic: any) => this.addId(comic))
    );
  }

  getFirstByVolume (publisher: string, series: string, volume: string): Observable<Comic> {
    const params = new HttpParams({
      fromObject: {
        publisher,
        series,
        volume
      }
    });
    return this.http.get<Comic>('api/comics/search/findFirstByPublisherAndSeriesAndVolumeOrderByPosition', { params }).pipe(
      map((comic: any) => this.addId(comic))
    );
  }

  scan (): Observable<any> {
    return this.http.get('api/scan');
  }

  listLastReadByVolume (): Observable<Comic[]> {
    return this.http.get('api/comics/search/findAllLastReadPerVolume').pipe(
      this.consumeHateoas(),
      map((data: any) => data.map((comic) => this.addId(comic)))
    );
  }

  update (comic: Comic): Observable<Comic> {
    return this.http.put<Comic>(`api/comics/${ comic.id }`, comic);
  }

  markAsRead (comic: Comic): Observable<any> {
    return this.http.put<Comic>('api/comics/markAsRead', comic).pipe(
      map((result: Comic) => this.addId(result))
    );
  }

  markAsUnread (comic: Comic): Observable<any> {
    return this.http.put<Comic>('api/comics/markAsUnread', comic).pipe(
      map((result: Comic) => this.addId(result))
    );
  }

  deleteComics (): Observable<any> {
    return this.http.delete('api/comics');
  }

  deleteProgress (): Observable<any> {
    return this.http.delete('api/progress');
  }

  deleteProgressForCurrentUser (): Observable<any> {
    return this.http.delete('api/progress/me');
  }

  bundleVolumes (): Observable<any> {
    return this.http.get('api/comics/bundle');
  }
}
