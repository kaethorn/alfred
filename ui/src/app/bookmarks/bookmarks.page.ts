import { Component } from '@angular/core';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { PopoverController } from '@ionic/angular';

import { BookmarkActionsComponent } from './bookmark-actions/bookmark-actions.component';
import { ComicsService } from '../comics.service';
import { Comic } from '../comic';

@Component({
  selector: 'app-bookmarks',
  templateUrl: './bookmarks.page.html',
  styleUrls: ['./bookmarks.page.sass']
})
export class BookmarksPage {

  comics: Comic[];

  constructor (
    private comicsService: ComicsService,
    private sanitizer: DomSanitizer,
    private popoverController: PopoverController
  ) { }

  ionViewDidEnter () {
    this.list();
  }

  private list () {
    this.comicsService.listLastReadByVolume().subscribe((comics: Comic[]) => {
      this.comics = comics;
    });
  }

  thumbnail (comic: Comic): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(`data:image/jpeg;base64,${ comic.thumbnail }`);
  }

  async openMenu (event: any, comic: Comic) {
    const popover = await this.popoverController.create({
      component: BookmarkActionsComponent,
      componentProps: { comic },
      event,
      translucent: true
    });
    popover.onWillDismiss().finally(() => {
      this.list();
    });
    await popover.present();
  }
}
