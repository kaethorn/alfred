import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';

import { ComicsService } from './../comics.service';
import { Comic } from './../comic';

@Component({
  selector: 'app-full-screen-reader',
  templateUrl: './full-screen-reader.component.html',
  styleUrls: ['./full-screen-reader.component.sass']
})
export class FullScreenReaderComponent implements OnInit {

  comic: Comic = {} as Comic;
  imagePath: string;
  private currentPage: number = 1;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.currentPage = Number.parseInt(params.get('page'));
      this.getComic(Number.parseInt(params.get('id')));
    });
  }

  private rightHalf(event: MouseEvent) {
    return (event.offsetX > (<HTMLElement>event.currentTarget).offsetWidth / 2) ? true : false;
  }

  public onClick (event: MouseEvent) : void {
    if (this.rightHalf(event)) {
      this.nextPage();
    } else {
      this.prevPage();
    }
  }

  private prevPage () : void {
    this.currentPage -= (this.currentPage > 1 ? 1 : 0);
    this.navigate(this.comic.id, this.currentPage);
  }

  private nextPage () : void {
    this.currentPage += (this.currentPage < this.comic.pageCount ? 1 : 0);
    this.navigate(this.comic.id, this.currentPage);
  }

  private navigate(id: number, page: number) : void {
    this.router.navigate(['/read-full-screen/', id, page]);
    this.imagePath = `/api/read/${ id }/${ page }`;
  }

  private getComic (id: number) : void {
    this.comicsService.get(id)
      .subscribe((data: Comic) => {
        this.comic = data;
        this.navigate(this.comic.id, this.currentPage);
      });
  }
}
