import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Pipe({
  name: 'secure'
})
export class SecurePipe implements PipeTransform {

  constructor(
    private http: HttpClient,
    private sanitizer: DomSanitizer
  ) { }

  public transform(url: string): Observable<SafeUrl> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: `Bearer ${ token }` });
    return this.http.get(url, { headers, responseType: 'blob' }).pipe(
      map(val => this.sanitizer.bypassSecurityTrustUrl(URL.createObjectURL(val)))
    );
  }
}
