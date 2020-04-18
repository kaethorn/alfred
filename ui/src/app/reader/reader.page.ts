import { Component, ViewChild, ElementRef, HostListener } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastController } from '@ionic/angular';

import { Comic } from '../comic';
import { ComicStorageService } from '../comic-storage.service';
import { NavigatorService, NavigationInstruction, AdjacentComic, PageSource } from '../navigator.service';

interface IOpenOptions {
  showToast?: boolean;
}

@Component({
  selector: 'app-reader',
  styleUrls: [ './reader.page.sass' ],
  templateUrl: './reader.page.html'
})
export class ReaderPage {

  @ViewChild('pagesLayer', { static: true }) public pagesLayer: ElementRef;
  public comic: Comic = {} as Comic;
  public imageSets: PageSource[][];
  public showControls = false;
  public transformation = {};
  private parent: string;
  private initialNavigation = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private navigator: NavigatorService,
    private toastController: ToastController,
    private comicStorageService: ComicStorageService
  ) { }

  @HostListener('document:keyup.esc', [ '$event' ])
  public handleEscape(): void {
    this.back();
  }

  @HostListener('document:keyup.arrowleft', [ '$event' ])
  public handleLeft(): void {
    this.go(-1);
  }
  @HostListener('document:keyup.arrowright', [ '$event' ])
  public handleRight(): void {
    this.go(1);
  }

  public ionViewDidEnter(): void {
    const comicId = this.route.snapshot.params.id;
    this.parent = this.route.snapshot.queryParams.parent || '/library/publishers';
    this.comicStorageService.get(comicId)
      .then(comic => {
        this.comic = comic;
        this.setup(this.comic);
        this.comicStorageService.storeSurrounding(comicId).then(() => {
          this.showToast('Volume cached.');
        });
      }).catch(() => {
        this.showToast('Comic book not available, please try again later.', 4000);
        this.back();
      });
  }

  public onClick(event: MouseEvent): void {
    const direction = this.getDirection(event);
    if (direction === 0) {
      this.toggleControls();
    } else {
      this.go(direction);
    }
  }

  public go(direction: number, event?: MouseEvent): void {
    this.navigate(this.navigator.go(direction));
    if (event) {
      event.stopPropagation();
    }
  }

  public openNext(options: IOpenOptions = {}): void {
    this.open('nextId', options);
  }

  public openPrevious(options: IOpenOptions = {}): void {
    this.open('previousId', options);
  }

  public toggleControls(): void {
    this.showControls = !this.showControls;
  }

  public onSwipe(direction: number): void {
    this.navigate(this.navigator.go(direction));
  }

  public back(): void {
    this.router.navigate([ this.parent ]);
  }

  public imageLoaded(image: PageSource): void {
    image.loaded = true;
  }

  private navigate(instruction: NavigationInstruction): void {
    switch (instruction.adjacent) {
      case AdjacentComic.same:
        this.comic.currentPage = NavigatorService.page;
        this.router.navigate([], {
          queryParams: { page: NavigatorService.page },
          queryParamsHandling: 'merge',
          relativeTo: this.route
        });
        this.setTransformation();
        this.comicStorageService.saveProgress(this.comic);
        break;
      case AdjacentComic.next:
        this.openNext({ showToast: true });
        break;
      case AdjacentComic.previous:
        this.openPrevious({ showToast: true });
        break;
    }
  }

  private setImage(image: PageSource): void {
    this.comicStorageService.getPageUrl(this.comic.id, image.page).then(url => {
      image.src = url;
    });
  }

  private setup(comic: Comic): void {
    this.comic = comic;
    this.imageSets = this.navigator.set(
      this.comic.pageCount,
      this.getPage(this.comic),
      this.isSideBySide()
    );
    for (const set of this.imageSets) {
      for (const image of set) {
        this.setImage(image);
      }
    }
    this.navigate(this.navigator.go());
  }

  private isSideBySide(): boolean {
    const parentElement = this.pagesLayer.nativeElement.parentElement;
    return parentElement.clientWidth > parentElement.clientHeight;
  }

  private getPage(comic: Comic): number {
    if (comic.currentPage === null || comic.currentPage === undefined) {
      return Number.parseInt(this.route.snapshot.queryParams.page, 10) || 0;
    }
    return comic.currentPage;
  }

  private setTransformation(): void {
    const currentSet = this.navigator.getSet();

    this.transformation = {
      transform: `translateX(-${ currentSet }00vw)`,
      transition: this.initialNavigation ? '' : 'transform 0.8s ease-out'
    };

    if (this.initialNavigation) {
      this.initialNavigation = false;
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
  private getDirection(event: MouseEvent): number {
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

  private open(adjacentAttr: string, options?: IOpenOptions): void {
    if (this.comic[adjacentAttr]) {
      this.comicStorageService.storeSurrounding(this.comic[adjacentAttr]);
      this.router.navigate([ '/read', this.comic[adjacentAttr] ], {
        queryParamsHandling: 'merge',
        relativeTo: this.route,
        replaceUrl: true
      });
      if (options.showToast) {
        if (adjacentAttr === 'nextId') {
          this.showToast(`Opening next issue of ${ this.comic.series } (${ this.comic.volume }).`);
        } else {
          this.showToast(`Opening previous issue of ${ this.comic.series } (${ this.comic.volume }).`);
        }
      }
    }
  }

  private async showToast(message: string, duration = 3000): Promise<void> {
    const toast = await this.toastController.create({
      duration,
      message
    });
    toast.present();
  }
}
