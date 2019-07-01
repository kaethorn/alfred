import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ComicsService } from '../comics.service';
import { NavigatorService } from '../navigator.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-browser',
  templateUrl: './browser.page.html',
  styleUrls: ['./browser.page.sass']
})
export class BrowserPage {

  comic: Comic = {} as Comic;
  imagePath: string;
  currentPage = 0;
  parent: string;

  constructor (
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService,
    private navigator: NavigatorService
  ) { }

  ionViewDidEnter () {
    this.parent = this.route.snapshot.queryParams.parent;
    this.currentPage = Number.parseInt(this.route.snapshot.queryParams.page, 10) || 0;
    this.comicsService.get(this.route.snapshot.params.id).subscribe((data: Comic) => {
      this.comic = data;
      this.navigator.set(
        this.comic.pageCount,
        this.currentPage,
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

  private navigate (): void {
    this.router.navigate(['/browse', this.comic.id], { queryParams: { page: NavigatorService.page } });
    this.imagePath = `/api/read/${ this.comic.id }/${ NavigatorService.page }`;
  }
}
