import { Component, ViewChild, ElementRef, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ComicsService } from '../comics.service';
import { NavigatorService, NavigationInstruction, AdjacentComic } from '../navigator.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-reader',
  templateUrl: './reader.page.html',
  styleUrls: ['./reader.page.sass']
})
export class ReaderPage {

  comic: Comic = {} as Comic;
  imagePathLeft = '';
  imagePathRight = '';
  showControls = false;
  parent: string;

  constructor (
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService,
    private navigator: NavigatorService,
  ) { }

  @ViewChild('pagesLayer') pagesLayer: ElementRef;

  @HostListener('document:keyup.esc', ['$event'])
  handleEscape () {
    this.router.navigate([this.parent]);
  }

  @HostListener('document:keyup.arrowleft', ['$event'])
  handleLeft () {
    this.go(-1);
  }
  @HostListener('document:keyup.arrowright', ['$event'])
  handleRight () {
    this.go(1);
  }

  ionViewDidEnter () {
    this.comicsService.get(this.route.snapshot.params.id).subscribe((data: Comic) => {
      this.comic = data;
      this.parent = this.route.snapshot.queryParams.parent || '/library/publishers';
      const parentElement = this.pagesLayer.nativeElement.parentElement;
      this.navigator.set(
        this.comic.pageCount,
        this.getPage(this.comic),
        (parentElement.clientWidth > parentElement.clientHeight) ? true : false
      );
      this.navigate(this.navigator.go());
    });
  }

  private getPage (comic: Comic): number {
    if (comic.currentPage === null || comic.currentPage === undefined) {
      return Number.parseInt(this.route.snapshot.queryParams.page, 10) || 0;
    }
    return comic.currentPage;
  }

  public onClick (event: MouseEvent): void {
    const direction = this.getDirection(event);
    if (direction === 0) {
      this.toggleControls();
    } else {
      this.go(direction);
    }
  }

  public go (direction: number, event?: MouseEvent): void {
    this.navigate(this.navigator.go(direction));
    if (event) {
      event.stopPropagation();
    }
  }

  public openNext () {
    if (this.comic.nextId) {
      this.router.navigate(['/read', this.comic.nextId], {
        relativeTo: this.route,
        queryParamsHandling: 'merge'
      });
    }
  }

  public openPrevious () {
    if (this.comic.previousId) {
      this.router.navigate(['/read', this.comic.previousId], {
        relativeTo: this.route,
        queryParamsHandling: 'merge'
      });
    }
  }

  public toggleControls (): void {
    this.showControls = !this.showControls;
  }

  public onSwipe (direction): void {
    this.navigate(this.navigator.go(direction));
  }

  public back (): void {
    this.router.navigate([this.parent]);
  }

  private navigate (instruction: NavigationInstruction) {
    switch (instruction.adjacent) {
      case AdjacentComic.same:
        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: { page: NavigatorService.page },
          queryParamsHandling: 'merge'
        });
        this.imagePathLeft = `/api/read/${ this.comic.id }/${ NavigatorService.page }`;
        this.imagePathRight = instruction.sideBySide ? `/api/read/${ this.comic.id }/${ NavigatorService.page + 1 }` : null;
        break;
      case AdjacentComic.next:
        this.openNext();
        break;
      case AdjacentComic.previous:
        this.openPrevious();
        break;
    }
  }

  /**
   * Determine the direction of page navigation.
   *
   * Evaluates the position the user clicked on the page. Clicking on the
   * left side of the page navigates back, clicking on the right side navigates
   * forward and clicking in the center shows controls.
   *
   * @returns `-1` for backwards, `1` for backwards and `0` for no navigation.
   */
  private getDirection (event: MouseEvent): number {
    const center = (<HTMLElement>event.currentTarget).offsetWidth / 2;
    const leftSide = center * 0.5;
    const rightSide = center * 1.5;
    if (event.clientX < leftSide) {
      return -1;
    } else if (event.clientX > rightSide) {
      return 1;
    } else {
      return 0;
    }
  }
}
