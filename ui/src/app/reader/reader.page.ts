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
export class ReaderPage implements OnInit {

  comic: Comic = {} as Comic;
  imagePathLeft: string;
  imagePathRight: string;
  showPageControls = false;
  showComicControls = false;
  parent: string[];

  constructor (
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService,
    private navigator: NavigatorService,
  ) { }

  @ViewChild('pagesLayer') pagesLayer: ElementRef;

  @HostListener('document:keyup.esc', ['$event'])
  handleEscape () {
    this.router.navigate(['/browse', this.comic.id], {
      relativeTo: this.route,
      queryParams: { page: NavigatorService.page },
      queryParamsHandling: 'merge'
    });
  }

  ngOnInit () {
    this.comicsService.get(this.route.snapshot.params.id).subscribe((data: Comic) => {
      this.comic = data;
      this.setParent(this.comic);
      const parentElement = this.pagesLayer.nativeElement.parentElement;
      this.navigator.set(
        this.comic.pageCount,
        Number.parseInt(this.route.snapshot.queryParams.page, 10) || 0,
        (parentElement.clientWidth > parentElement.clientHeight) ? true : false
      );
      this.navigate(this.navigator.go());
    });
  }

  private setParent (comic: Comic) {
    const parent = this.route.snapshot.queryParams.parent;
    switch (parent) {
      case '/issues':
        this.parent = [parent, comic.publisher, comic.series, comic.volume];
        break;
      // FIXME this isn't really a route. Is there better way to pass one the angular way?
      case '/volumes':
        this.parent = ['/library/publishers', comic.publisher, 'series', comic.series, 'volumes'];
        break;
      default:
        this.parent = ['/library/publishers'];
        break;
    }
  }

  public onClick (event: MouseEvent): void {
    const direction = this.getDirection(event);
    if (direction === 0) {
      this.togglePageControls();
    } else {
      this.go(direction);
    }
  }

  public onControlClick (event: MouseEvent): void {
    this.showComicControls = false;
    this.showPageControls = false;
  }

  public go (direction: number, event?: MouseEvent): void {
    this.navigate(this.navigator.go(direction));
    if (event) {
      event.stopPropagation();
    }
  }

  private togglePageControls (): void {
    this.showPageControls = !this.showPageControls;
  }

  private toggleComicControls (): void {
    this.showComicControls = !this.showComicControls;
  }

  public onSwipe (direction): void {
    this.navigate(this.navigator.go(direction));
  }

  public back (): void {
    this.router.navigate(this.parent);
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
        // FIXME When `sideBySide` is `false`, `this.imagePathRight` is `null` but is still
        // rendered in the view, resulting in error:
        // `GET http://localhost:4200/null 404 (Not Found)`.
        this.imagePathRight = instruction.sideBySide ? `/api/read/${ this.comic.id }/${ NavigatorService.page + 1 }` : null;
        break;
      case AdjacentComic.next:
        this.toggleComicControls();
        break;
      case AdjacentComic.previous:
        this.toggleComicControls();
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
