import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ComicsService {
  constructor(private http: HttpClient) {}

  scanUrl = 'api/scan';

  getComics() {
    return this.http.get(this.scanUrl);
  }
}
