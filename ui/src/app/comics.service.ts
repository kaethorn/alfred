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
  private readonly comicMarkAsReadUrl = 'api/comics/markAsRead';
  private readonly comicMarkAsUnreadUrl = 'api/comics/markAsUnread';

  private consumeHateoas (): any {
    return map((data: any) => data._embedded.comics);
  }

  private addId(item: any): any {
    item.id = item._links.self.href.split('/').pop();
    return item;
  }

  list (): Observable<Comic[]> {
    return this.http.get(this.comicsUrl).pipe(
      this.consumeHateoas(),
      map((data: any) => data.map((comic) => this.addId(comic)))
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
      this.consumeHateoas(),
      map((data: any) => data.map((comic) => this.addId(comic)))
    );
  }

  get (id: string): Observable<Comic> {
    return this.http.get<Comic>(`${ this.comicUrl }/${ id }`).pipe(
      map((comic: any) => this.addId(comic))
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
      map((comic: any) => this.addId(comic))
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
      map((comic: any) => this.addId(comic))
    );
  }

  scan (): Observable<any> {
    return this.http.get(this.scanUrl);
  }

  listLastReadByVolume(): Observable<Comic[]> {
    return this.http.get(this.lastUnreadsUrl).pipe(
      this.consumeHateoas(),
      map((data: any) => data.map((comic) => this.addId(comic)))
    );
  }

  update (comic: Comic): Observable<Comic> {
    return this.http.put<Comic>(`${ this.comicUrl }/${ comic.id }`, comic);
  }

  markAsRead (comic: Comic): Observable<any> {
    return this.http.put<Comic>(`${ this.comicMarkAsReadUrl }`, comic).pipe(
      map((result: Comic) => this.addId(result))
    );
  }

  markAsUnread (comic: Comic): Observable<any> {
    return this.http.put<Comic>(`${ this.comicMarkAsUnreadUrl }`, comic).pipe(
      map((result: Comic) => this.addId(result))
    );
  }
}
