import { Component, OnInit, ViewChild, ElementRef, HostListener } from '@angular/core';
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
  currentPage = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService
  ) { }

  @ViewChild('layer') layer: ElementRef;

  @HostListener('document:keyup.esc', ['$event'])
  handleEscape() {
    this.exitFullScreen(this.comic.id, this.currentPage);
  }

  ngOnInit () {
    const parentElement = this.layer.nativeElement.parentElement;
    this.sideBySide = (parentElement.clientWidth > parentElement.clientHeight) ? true : false;

    this.currentPage = Number.parseInt(this.route.snapshot.params.page, 10);
    this.getComic(this.route.snapshot.params.id);
  }

  public onClick (event: MouseEvent): void {
    if (this.rightHalf(event)) {
      this.nextPage();
    } else {
      this.prevPage();
    }
  }

  // FIXME
  // Clean up all these methods. They are tightly coupled and have business logic
  // shared between each other. Also compare with reader.component.ts.
  private rightHalf(event: MouseEvent) {
    return (event.clientX > (<HTMLElement>event.currentTarget).offsetWidth / 2) ? true : false;
  }

  private prevPage (): void {
    this.currentPage -= this.currentPage > 0 ? 1 : 0;
    this.currentPage -= (this.sideBySide && this.currentPage > 0) ? 1 : 0;
    this.navigate(this.comic.id, this.currentPage);
  }

  private nextPage (): void {
    const increment = (this.sideBySide && this.currentPage > 0) ? 2 : 1;
    this.currentPage += (this.currentPage + increment) < this.comic.pageCount ? increment : 0;
    this.navigate(this.comic.id, this.currentPage);
  }

  private navigate(id: number, page: number): void {
    const sideBySide = this.sideBySide && page > 0 && page < (this.comic.pageCount - 1);
    this.router.navigate(['/read-full-screen/', id, page ]);
    this.imagePathLeft = `/api/read/${ id }/${ page }`;
    this.imagePathRight = sideBySide ? `/api/read/${ id }/${ page + 1 }` : null;
  }

  private getComic (id: string): void {
    this.comicsService.get(id)
      .subscribe((data: Comic) => {
        this.comic = data;
        this.navigate(this.comic.id, this.currentPage);
      });
  }

  private exitFullScreen (id: number, page: number): void {
    this.router.navigate(['/read/', id, page ]);
  }
}
