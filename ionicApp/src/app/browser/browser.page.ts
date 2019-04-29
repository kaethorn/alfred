import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ComicsService } from '../comics.service';
import { NavigatorService } from '../navigator.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-browser',
  templateUrl: './browser.page.html',
  styleUrls: ['./browser.page.sass']
})
export class BrowserPage implements OnInit {

  comic: Comic = {} as Comic;
  imagePath: string;
  currentPage = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService,
    private navigator: NavigatorService
  ) {}

  ngOnInit() {
    this.currentPage = Number.parseInt(this.route.snapshot.params.page, 10) || 0;
    this.comicsService.get(this.route.snapshot.params.id).subscribe((data: Comic) => {
      this.comic = data;
      this.navigator.set(
        this.comic.pageCount,
        Number.parseInt(this.route.snapshot.params.page, 10) || 0,
        false
      );
      this.navigator.go();
      this.navigate();
    });
  }

  public go (offset: number): void {
    this.navigator.go(offset);
    this.navigate();
  }

  private navigate(): void {
    this.router.navigate(['/browse', this.comic.id, NavigatorService.page]);
    this.imagePath = `/api/read/${ this.comic.id }/${ NavigatorService.page }`;
  }
}
