import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Thumbnail } from './thumbnail';

@Injectable({
  providedIn: 'root'
})
export class ThumbnailsService {

  constructor (
    private sanitizer: DomSanitizer,
    private http: HttpClient
  ) {}

  get (comicId: string): Observable<SafeUrl> {
    return this.http.get<Thumbnail>(`api/thumbnails/${ comicId }`).pipe(
      map((thumbnail: any) => {
        return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ thumbnail.thumbnail }`);
      })
    );
  }
}
