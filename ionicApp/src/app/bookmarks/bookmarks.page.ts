import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

import { ComicsService } from '../comics.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-bookmarks',
  templateUrl: './bookmarks.page.html',
  styleUrls: ['./bookmarks.page.sass']
})
export class BookmarksPage implements OnInit {

  comics: Comic[];

  constructor(
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
    });
  }

  thumbnail (comic: Comic): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ comic.thumbnail }`);
  }
}
