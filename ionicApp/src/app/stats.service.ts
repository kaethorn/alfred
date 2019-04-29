import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Stats } from './stats';

@Injectable({
  providedIn: 'root'
})
export class StatsService {

  constructor(
    private http: HttpClient
  ) {}

  get(): Observable<Stats> {
    return this.http.get<Stats>('api/stats').pipe(
      map((stats: any) => {
        return {
          issues: stats.issues,
          publishers: stats.publishers,
          series: stats.series,
          volumes: stats.volumes,
          users: stats.users
        };
      })
    )
  }
}
