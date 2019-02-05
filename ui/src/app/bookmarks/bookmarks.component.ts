import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarRef, SimpleSnackBar } from '@angular/material';

import { BookmarksService } from '../bookmarks.service';
import { Bookmark } from '../bookmark';

@Component({
  selector: 'app-bookmarks',
  templateUrl: './bookmarks.component.html',
  styleUrls: ['./bookmarks.component.sass']
})
export class BookmarksComponent implements OnInit {

  snackBarRef: MatSnackBarRef<SimpleSnackBar>;
  bookmarks: Bookmark[];

  constructor(
    private snackBar: MatSnackBar,
    private router: Router,
    private bookmarksService: BookmarksService
  ) {}

  ngOnInit() {
    this.list();
  }

  private list () {
    this.bookmarksService.list().subscribe((bookmarks: Bookmark[]) => {
      this.bookmarks = bookmarks;
      if (!this.bookmarks.length) {
        this.snackBarRef = this.snackBar.open('No bookmarks found', 'Get started', { duration: 100000 });
        this.snackBarRef.onAction().subscribe(() => {
          this.router.navigate(['/library']);
        });
      }
    });
  }
}
