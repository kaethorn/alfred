import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Comic, ScannerIssue } from './comic';

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
    return this.http.get('api/comics/search/findAllByOrderByPublisherAscSeriesAscVolumeAscPositionAsc').pipe(
      this.consumeHateoas(),
      map((data: any) => data.map(this.addId))
    );
  }

  listComicsWithErrors (): Observable<Comic[]> {
    return this.http.get('api/queue').pipe(
      this.consumeHateoas(),
      map((data: any) => data.map(this.addId))
    );
  }

  listComicsWithoutErrors (): Observable<Comic[]> {
    return this.http.get('api/queue/valid').pipe(
      this.consumeHateoas(),
      map((data: any) => data.map(this.addId))
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
      map((data: any) => data.map(this.addId))
    );
  }

  get (id: string): Observable<Comic> {
    return this.http.get<Comic>(`api/comics/${ id }`).pipe(
      map(this.addId)
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
      map(this.addId)
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
      map(this.addId)
    );
  }

  scan (): Observable<any> {
    return this.http.get('api/scan');
  }

  listLastReadByVolume (): Observable<Comic[]> {
    return this.http.get('api/comics/search/findAllLastReadPerVolume').pipe(
      this.consumeHateoas(),
      map((data: any) => data.map(this.addId))
    );
  }

  update (comic: Comic): Observable<Comic> {
    return this.http.put<Comic>('api/comics', comic).pipe(
      map(this.addId)
    );
  }

  updateProgress (comic: Comic): Observable<Comic> {
    return this.http.put<Comic>('api/comics/progress', comic).pipe(
      map(this.addId)
    );
  }

  scrape (comic: Comic): Observable<Comic> {
    return this.http.put<Comic>('api/comics/scrape', comic).pipe(
      map(this.addId)
    );
  }

  markAsRead (comic: Comic): Observable<any> {
    return this.http.put<Comic>('api/comics/markAsRead', comic).pipe(
      map(this.addId)
    );
  }

  markAsUnread (comic: Comic): Observable<any> {
    return this.http.put<Comic>('api/comics/markAsUnread', comic).pipe(
      map(this.addId)
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

  deletePage (comic: Comic, path: string): Observable<Comic> {
    return this.http.delete<Comic>(`api/comics/${ comic.id }/page/${ encodeURIComponent(path) }`).pipe(
      map(this.addId)
    );
  }

  bundleVolumes (): Observable<any> {
    return this.http.get('api/comics/bundle');
  }

  getPage (comicId: string, page: number): Observable<Blob> {
    return this.http.get(`/api/download/${ comicId }/${ page }`, {
      responseType: 'blob'
    });
  }

  fixIssue (comic: Comic, error: ScannerIssue): Observable<Comic> {
    return this.http.put(`/api/queue/fix/${ error.type }`, comic).pipe(
      map(this.addId)
    );
  }
}
