import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';
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

  getFrontCover (comicId: string): Observable<Thumbnail> {
    return this.http.get<Thumbnail>(`api/thumbnails/front-cover/${ comicId }`).pipe(
      map((thumbnail: any) => {
        thumbnail.url = this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ thumbnail.thumbnail }`);
        return thumbnail;
      })
    );
  }

  getBackCover (comicId: string): Observable<Thumbnail> {
    return this.http.get<Thumbnail>(`api/thumbnails/back-cover/${ comicId }`).pipe(
      map((thumbnail: any) => {
        thumbnail.url = this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ thumbnail.thumbnail }`);
        return thumbnail;
      })
    );
  }
}
