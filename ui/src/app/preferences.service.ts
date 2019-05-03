import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Preference } from './preference';

@Injectable({
  providedIn: 'root'
})
export class PreferencesService {

  constructor (
    private http: HttpClient
  ) { }

  private consumeHateoas (): any {
    return map((data: any) => data._embedded.preferences);
  }

  private addId (item: any): any {
    item.id = item._links.self.href.split('/').pop();
    return item;
  }

  list (): Observable<Preference[]> {
    return this.http.get('api/preferences').pipe(
      this.consumeHateoas(),
      map((data: any) => data.map((item) => this.addId(item)))
    );
  }

  get (key: string): Observable<Preference> {
    return this.http.get<Preference>(`api/preferences/search/findByKey?key=${ key }`).pipe(
      map((item: any) => this.addId(item))
    );
  }

  update (preference: Preference): Observable<Preference> {
    return this.http.put<Preference>(`api/preferences/${ preference.id }`, preference);
  }
}
