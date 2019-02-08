import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ComicsService } from './../comics.service';
import { Comic } from './../comic';

@Component({
  selector: 'app-browser',
  templateUrl: './browser.component.html',
  styleUrls: ['./browser.component.sass']
})
export class BrowserComponent implements OnInit {

  comic: Comic = {} as Comic;
  imagePath: string;
  currentPage = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService
  ) {}

  ngOnInit() {
    if (this.route.snapshot.params.publisher) {
      this.getFirstComic(
        this.route.snapshot.params.publisher,
        this.route.snapshot.params.series,
        this.route.snapshot.params.volume
      );
    } else {
      this.currentPage = Number.parseInt(this.route.snapshot.params.page, 10) || 0;
      this.getComic(this.route.snapshot.params.id);
    }
  }

  public prevPage (): void {
    this.currentPage -= (this.currentPage > 0 ? 1 : 0);
    this.navigate(this.comic.id, this.currentPage);
  }

  public nextPage (): void {
    this.currentPage += (this.currentPage < this.comic.pageCount ? 1 : 0);
    this.navigate(this.comic.id, this.currentPage);
  }

  private navigate(id: string, page: number): void {
    this.router.navigate(['/browse/', id, page]);
    this.imagePath = `/api/read/${ id }/${ page }`;
  }

  private getComic (id: string): void {
    this.comicsService.get(id)
      .subscribe((data: Comic) => {
        this.comic = data;
        this.navigate(this.comic.id, this.currentPage);
      });
  }

  private getFirstComic (publisher: string, series: string, volume: string) {
    this.comicsService.listByVolume(publisher, series, volume)
      .subscribe((data: Comic[]) => {
        this.comic = data[0];
        this.router.navigate(['/read/', this.comic.id, this.currentPage], { replaceUrl: true });
      });
  }
}
