import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Preference } from './preference';

@Injectable({
  providedIn: 'root'
})
export class PreferencesService {
  constructor(private http: HttpClient) {}

  private API_PREFIX: String = 'api';

  private listUrl: string = `${ this.API_PREFIX }/preferences`;
  private getUrl: string = `${ this.API_PREFIX }/preferences/search/findByKey?key=`;

  list () : Observable<Preference[]> {
    return this.http.get(this.listUrl).pipe(
      map((data: any) => data._embedded.preferences),
      map((data: any) => {
        return data.map((item) => {
          item.id = item._links.self.href.split('/').pop();
          return item;
        });
      })
    );
  }

  get (key: string) : Observable<Preference> {
    return this.http.get<Preference>(`${ this.getUrl }${ key }`).pipe(
      map((item: any) => {
        item.id = item._links.self.href.split('/').pop();
        return item;
      })
    );
  }

  update (preference: Preference) : Observable<Preference> {
    return this.http.put<Preference>(`${ this.listUrl }/${ preference.id }`, preference);
  }
}
