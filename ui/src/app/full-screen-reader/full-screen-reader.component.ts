import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ComicsService } from './../comics.service';
import { Comic } from './../comic';

@Component({
  selector: 'app-full-screen-reader',
  templateUrl: './full-screen-reader.component.html',
  styleUrls: ['./full-screen-reader.component.sass']
})
export class FullScreenReaderComponent implements OnInit {

  comic: Comic = {} as Comic;
  imagePathLeft: string;
  imagePathRight: string;
  sideBySide: boolean;
  currentPage: number = 1;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService
  ) { }

  @ViewChild('layer') layer: ElementRef;

  ngOnInit () {
    const parentElement = this.layer.nativeElement.parentElement;
    this.sideBySide = (parentElement.clientWidth > parentElement.clientHeight) ? true : false;

    this.currentPage = Number.parseInt(this.route.snapshot.params.page);
    this.getComic(Number.parseInt(this.route.snapshot.params.id));
  }

  private rightHalf(event: MouseEvent) {
    return (event.clientX > (<HTMLElement>event.currentTarget).offsetWidth / 2) ? true : false;
  }

  public onClick (event: MouseEvent) : void {
    if (this.rightHalf(event)) {
      this.nextPage();
    } else {
      this.prevPage();
    }
  }

  private prevPage () : void {
    this.currentPage -= this.currentPage > 1 ? 1 : 0;
    this.currentPage -= (this.sideBySide && this.currentPage > 1) ? 1 : 0;
    this.navigate(this.comic.id, this.currentPage);
  }

  private nextPage () : void {
    this.currentPage += this.currentPage < this.comic.pageCount ? 1 : 0;
    this.currentPage += (this.sideBySide && this.currentPage > 2) ? 1 : 0;
    this.navigate(this.comic.id, this.currentPage);
  }

  private navigate(id: number, page: number) : void {
    const sideBySide = this.sideBySide && page > 1;
    this.router.navigate(['/read-full-screen/', id, page ]);
    this.imagePathLeft = `/api/read/${ id }/${ page }`;
    this.imagePathRight = sideBySide ? `/api/read/${ id }/${ page + 1 }` : null;
  }

  private getComic (id: number) : void {
    this.comicsService.get(id)
      .subscribe((data: Comic) => {
        this.comic = data;
        this.navigate(this.comic.id, this.currentPage);
      });
  }
}
