import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Bookmark } from './bookmark';

@Injectable({
  providedIn: 'root'
})
export class BookmarksService {
  constructor(private http: HttpClient) {}

  private API_PREFIX: String = 'api';

  private listUrl = `${ this.API_PREFIX }/bookmarks`;

  list (): Observable<Bookmark[]> {
    return this.http.get(this.listUrl).pipe(
      map((data: any) => data._embedded.bookmarks),
      map((data: any) => {
        return data.map((item) => {
          item.id = item._links.self.href.split('/').pop();
          return item;
        });
      })
    );
  }

  delete (id: string) {}
}
