import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Comic } from './comic';

@Injectable({
  providedIn: 'root'
})
export class ComicsService {
  constructor(private http: HttpClient) {}

  private API_PREFIX: String = 'api';

  private scanUrl: string = `${ this.API_PREFIX }/scan`;
  private listUrl: string = `${ this.API_PREFIX }/comics`;

  list() : Observable<Comic[]> {
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

  scan () {
    return this.http.get(this.scanUrl);
  }
}
