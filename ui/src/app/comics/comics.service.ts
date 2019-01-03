import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ComicsService {
  constructor(private http: HttpClient) {}

  private API_PREFIX: String = 'api';

  private scanUrl: string = `${ this.API_PREFIX }/scan`;
  private listUrl: string = `${ this.API_PREFIX }/comics`;

  list () {
    return this.http.get(this.listUrl);
  }

  scan () {
    return this.http.get(this.scanUrl);
  }
}
