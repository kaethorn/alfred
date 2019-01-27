import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Comic } from './comic';
import { Volume } from './volume';

@Injectable({
  providedIn: 'root'
})
export class ComicsService {
  constructor(private http: HttpClient) {}

  private API_PREFIX: String = 'api';

  private scanUrl: string = `${ this.API_PREFIX }/scan`;
  private listUrl: string = `${ this.API_PREFIX }/comics/search/findAllByOrderBySeriesAscVolumeAscPositionAsc`;
  private listByVolumeUrl: string = `${ this.API_PREFIX }/comics/search/findAllBySeriesAndVolume`;
  private getUrl: string = `${ this.API_PREFIX }/comics`;
  private listVolumesUrl: string = `${ this.API_PREFIX }/comics/search/findVolumesBySeries`;

  list () : Observable<Comic[]> {
    return this.http.get(this.listUrl).pipe(
      map((data: any) => data._embedded.comics),
      map((data: any) => {
        return data.map((comic) => {
          comic.id = comic._links.self.href.split('/').pop();
          return comic;
        });
      })
    );
  }

  listByVolume (series: string, volume: string) : Observable<Comic[]> {
    const params = new HttpParams({
      fromObject: {
        series: series,
        volume: volume
      }
    });
    return this.http.get(this.listByVolumeUrl, { params: params }).pipe(
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
    return this.http.get<Comic>(`${ this.getUrl }/${ id }`).pipe(
      map((comic: any) => {
        comic.id = comic._links.self.href.split('/').pop();
        return comic;
      })
    );
  }

  scan () {
    return this.http.get(this.scanUrl);
  }

  listVolumes () : Observable<Volume[]> {
    return this.http.get(this.listVolumesUrl).pipe(
      map((data: any) => data._embedded.volumes),
      map((data: any) => {
        return data.map((comic) => {
          comic.id = comic._links.self.href.split('/').pop();
          return comic;
        });
      })
    );
  }
}
