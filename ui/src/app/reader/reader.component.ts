import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { ComicsService } from './../comics.service';
import { Comic } from './../comic';

@Component({
  selector: 'app-reader',
  templateUrl: './reader.component.html',
  styleUrls: ['./reader.component.css']
})
export class ReaderComponent implements OnInit {

  comic: Comic = {} as Comic;
  imagePath: string;
  private currentPage: number = 1;

  constructor(
    private route: ActivatedRoute,
    private comicsService: ComicsService
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      this.getComic(Number.parseInt(params.get('id')));
    });
  }

  public prevPage () : void {
    this.currentPage -= 1;
    if (this.currentPage <= 0) {
      this.currentPage = 1;
    }
    this.setImagePath(this.comic.id, this.currentPage);
  }

  public nextPage () : void {
    this.currentPage += 1;
    if (this.currentPage > this.comic.page_count) {
      this.currentPage -= 1;
    }
    this.setImagePath(this.comic.id, this.currentPage);
  }

  private setImagePath (id: number, page: number) : void {
    this.imagePath = `/api/read/${ id }/${ page }`;
  }

  private getComic (id: number) : void {
    this.comicsService.get(id)
      .subscribe((data: Comic) => {
        this.comic = data;
        this.setImagePath(this.comic.id, this.currentPage);
      });
  }
}
