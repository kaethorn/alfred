import { Component, OnInit, ViewChild, ElementRef, HostListener } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ComicsService } from './../comics.service';
import { NavigatorService } from './../navigator.service';
import { Comic } from './../comic';

@Component({
  selector: 'app-reader',
  templateUrl: './reader.component.html',
  styleUrls: ['./reader.component.sass']
})
export class ReaderComponent implements OnInit {

  comic: Comic = {} as Comic;
  imagePathLeft: string;
  imagePathRight: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService,
    private navigator: NavigatorService
  ) {}

  @ViewChild('layer') layer: ElementRef;

  @HostListener('document:keyup.esc', ['$event'])
  handleEscape() {
    this.router.navigate(['/browse/', this.comic.id, NavigatorService.page ]);
  }

  ngOnInit () {
    this.comicsService.get(this.route.snapshot.params.id).subscribe((data: Comic) => {
      this.comic = data;
      const parentElement = this.layer.nativeElement.parentElement;
      this.navigator.set(
        this.comic.pageCount,
        Number.parseInt(this.route.snapshot.params.page, 10),
        (parentElement.clientWidth > parentElement.clientHeight) ? true : false
      );
      this.navigate(this.navigator.go());
    });
  }

  public onClick (event: MouseEvent): void {
    this.navigate(
      this.navigator.go(
        (event.clientX > (<HTMLElement>event.currentTarget).offsetWidth / 2) ?  1 : -1));
  }

  private navigate (sideBySide: boolean) {
    this.router.navigate(['/read/', this.comic.id, NavigatorService.page]);
    this.imagePathLeft = `/api/read/${ this.comic.id }/${ NavigatorService.page }`;
    this.imagePathRight = sideBySide ? `/api/read/${ this.comic.id }/${ NavigatorService.page + 1 }` : null;
    if (this.navigator.lastPage()) {
      this.comic.read = true;
      this.comic.lastRead = new Date();
      this.comicsService.update(this.comic)
        .subscribe(() => {});
    }
  }
}
