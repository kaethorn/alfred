import { Component, ViewChild, ElementRef, HostListener } from '@angular/core';
import { ToastController } from '@ionic/angular';
import { ActivatedRoute, Router } from '@angular/router';
import { trigger, state, style, animate, transition } from '@angular/animations';

import { NavigatorService, NavigationInstruction, AdjacentComic } from '../navigator.service';
import { Comic } from '../comic';
import { ComicStorageService } from '../comic-storage.service';

interface IOpenOptions {
  showToast?: boolean;
}

@Component({
  selector: 'app-reader',
  templateUrl: './reader.page.html',
  styleUrls: ['./reader.page.sass'],
  animations: [
    trigger('swapImages', [
      state('loaded', style({
        opacity: 1,
      })),
      state('initial', style({
        opacity: 0.0,
      })),
      transition('initial => loaded', [
        animate('1s')
      ]),
      transition('loaded => initial', [
        animate('0.5s')
      ])
    ])
  ]
})
export class ReaderPage {

  comic: Comic = {} as Comic;
  previousImagePathLeft = '';
  previousImagePathRight = '';
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
    const comicId = this.route.snapshot.params.id;
    this.parent = this.route.snapshot.queryParams.parent || '/library/publishers';
    this.comicStorageService.get(comicId)
      .then((comic) => {
        this.comic = comic;
        this.setup(this.comic);
      }).catch(() => {
        this.showToast('Comic book not available, please try again later.');
        this.back();
      });
    this.comicStorageService.storeSurrounding(comicId).then(() => {
      this.showToast('Volume cached.');
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

  onClick (event: MouseEvent): void {
    const direction = this.getDirection(event);
    if (direction === 0) {
      this.toggleControls();
    } else {
      this.go(direction);
    }
  }

  go (direction: number, event?: MouseEvent): void {
    this.navigate(this.navigator.go(direction));
    if (event) {
      event.stopPropagation();
    }
  }

  private open (adjacentAttr: string, options?: IOpenOptions) {
    if (this.comic[adjacentAttr]) {
      this.comicStorageService.storeSurrounding(this.comic[adjacentAttr]);
      this.router.navigate(['/read', this.comic[adjacentAttr]], {
        replaceUrl: true,
        relativeTo: this.route,
        queryParamsHandling: 'merge'
      });
      if (options.showToast) {
        this.showToast(`Opening next issue of ${ this.comic.series } (${ this.comic.volume }).`);
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

  openNext (options?: IOpenOptions) {
    this.open('nextId', options);
  }

  openPrevious (options?: IOpenOptions) {
    this.open('previousId', options);
  }

  toggleControls (): void {
    this.showControls = !this.showControls;
  }

  onSwipe (direction): void {
    this.navigate(this.navigator.go(direction));
  }

  back (): void {
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
    // FIXME set transition direction

    // Previous set
    if (this.imagePathLeft.length) {
      this.previousImagePathRight = this.imagePathLeft;
    }
    if (this.imagePathRight.length) {
      this.previousImagePathLeft = this.imagePathLeft;
    }

    // Current set
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
