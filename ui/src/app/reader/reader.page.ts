import { Component, ViewChild, ElementRef, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ComicsService } from '../comics.service';
import { NavigatorService } from '../navigator.service';
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
  showControl = false;

  constructor (
    private route: ActivatedRoute,
    private router: Router,
    private comicsService: ComicsService,
    private navigator: NavigatorService
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
      const parentElement = this.pagesLayer.nativeElement.parentElement;
      this.navigator.set(
        this.comic.pageCount,
        Number.parseInt(this.route.snapshot.queryParams.page, 10) || 0,
        (parentElement.clientWidth > parentElement.clientHeight) ? true : false
      );
      this.navigate(this.navigator.go());
    });
  }

  public onClick (event: MouseEvent): void {
    const offset = this.getDirection(event);
    if (offset === 0) {
      this.toggleControls();
    } else {
      this.go(offset);
    }
  }

  public onControlClick (event: MouseEvent): void {
    this.toggleControls();
  }

  public go (offset: number, event?: MouseEvent): void {
    this.navigate(this.navigator.go(offset));
    if (event) {
      event.stopPropagation();
    }
  }

  private toggleControls (): void {
    this.showControl = !this.showControl;
  }

  public onSwipe (offset): void {
    this.navigate(this.navigator.go(offset));
  }

  public back (event: MouseEvent): void {
    this.router.navigate(['/issues', this.comic.publisher, this.comic.series, this.comic.volume]);
  }

  private navigate (sideBySide: boolean) {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { page: NavigatorService.page },
      queryParamsHandling: 'merge'
    });
    this.imagePathLeft = `/api/read/${ this.comic.id }/${ NavigatorService.page }`;
    this.imagePathRight = sideBySide ? `/api/read/${ this.comic.id }/${ NavigatorService.page + 1 }` : null;
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
