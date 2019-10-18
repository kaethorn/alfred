import { Component, ViewChild, ElementRef, HostListener } from '@angular/core';
import { ToastController } from '@ionic/angular';
import { ActivatedRoute, Router } from '@angular/router';

import { NavigatorService, NavigationInstruction, AdjacentComic } from '../navigator.service';
import { Comic } from '../comic';
import { ComicStorageService } from '../comic-storage.service';

interface IOpenOptions {
  showToast?: boolean;
}

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
  private parent: string;

  constructor (
    private route: ActivatedRoute,
    private router: Router,
    private navigator: NavigatorService,
    private toastController: ToastController,
    private comicStorageService: ComicStorageService,
  ) { }

  @ViewChild('pagesLayer', { static: true }) pagesLayer: ElementRef;

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

  async ionViewDidEnter () {
    this.parent = this.route.snapshot.queryParams.parent || '/library/publishers';
    this.comicStorageService.cache(this.route.snapshot.params.id)
      .then((comic) => {
        this.comic = comic;
        this.setup(this.comic);
      }).catch(() => {
        this.showToast('Comic book not available, please try again later.');
        this.back();
      });
  }

  private setup (comic: Comic) {
    this.comic = comic;
    const parentElement = this.pagesLayer.nativeElement.parentElement;
    this.navigator.set(
      this.comic.pageCount,
      this.getPage(this.comic),
      (parentElement.clientWidth > parentElement.clientHeight) ? true : false
    );
    this.navigate(this.navigator.go());
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

  private open (adjacentAttr: string, options?: IOpenOptions) {
    if (this.comic[adjacentAttr]) {
      this.comicStorageService.storeSurrounding(this.comic[adjacentAttr]);
      this.router.navigate(['/read', this.comic[adjacentAttr]], {
        relativeTo: this.route,
        queryParamsHandling: 'merge'
      });
      if (options.showToast) {
        this.showToast(`Next up: ${ this.comic.series } (${ this.comic.volume }) #${ this.comic.number }`);
      }
    }
  }

  private async showToast (message: string, duration: number = 3000) {
    const toast = await this.toastController.create({
      message,
      duration
    });
    toast.present();
  }

  public openNext (options?: IOpenOptions) {
    this.open('nextId', options);
  }

  public openPrevious (options?: IOpenOptions) {
    this.open('previousId', options);
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
        this.setImages(instruction.sideBySide);
        break;
      case AdjacentComic.next:
        this.openNext({ showToast: true });
        break;
      case AdjacentComic.previous:
        this.openPrevious({ showToast: true });
        break;
    }
  }

  private async setImages (sideBySide: boolean) {
    this.imagePathLeft = await this.comicStorageService
      .readPage(this.comic.id, NavigatorService.page);
    if (sideBySide) {
      this.imagePathRight = await this.comicStorageService
        .readPage(this.comic.id, NavigatorService.page + 1);
    } else {
      this.imagePathRight = null;
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
