import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarRef, SimpleSnackBar } from '@angular/material';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { ComicsService } from '../comics.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-bookmarks',
  templateUrl: './bookmarks.component.html',
  styleUrls: ['./bookmarks.component.sass']
})
export class BookmarksComponent implements OnInit {

  snackBarRef: MatSnackBarRef<SimpleSnackBar>;
  comics: Comic[];

  constructor(
    private snackBar: MatSnackBar,
    private router: Router,
    private comicsService: ComicsService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.list();
  }

  private list () {
    this.comicsService.listLastReadByVolume().subscribe((comics: Comic[]) => {
      this.comics = comics;
      if (!this.comics.length) {
        this.snackBarRef = this.snackBar.open('No bookmarks found', 'Get started', { duration: 100000 });
        this.snackBarRef.onAction().subscribe(() => {
          this.router.navigate(['/library']);
        });
      }
    });
  }

  thumbnail (comic: Comic): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ comic.thumbnail }`);
  }
}
