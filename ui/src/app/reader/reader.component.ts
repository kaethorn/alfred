import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ComicsService } from './../comics.service';
import { Comic } from './../comic';

@Component({
  selector: 'app-reader',
  templateUrl: './reader.component.html',
  styleUrls: ['./reader.component.sass']
})
export class ReaderComponent implements OnInit {

  comic: Comic = {} as Comic;
  imagePath: string;
  private currentPage = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService
  ) { }

  ngOnInit() {
    this.currentPage = Number.parseInt(this.route.snapshot.params.page, 10);
    this.getComic(this.route.snapshot.params.id);
  }

  public prevPage (): void {
    this.currentPage -= (this.currentPage > 0 ? 1 : 0);
    this.navigate(this.comic.id, this.currentPage);
  }

  public nextPage (): void {
    this.currentPage += (this.currentPage < this.comic.pageCount ? 1 : 0);
    this.navigate(this.comic.id, this.currentPage);
  }

  private navigate(id: number, page: number): void {
    this.router.navigate(['/read/', id, page]);
    this.imagePath = `/api/read/${ id }/${ page }`;
  }

  private getComic (id: string): void {
    this.comicsService.get(id)
      .subscribe((data: Comic) => {
        this.comic = data;
        this.navigate(this.comic.id, this.currentPage);
      });
  }
}
