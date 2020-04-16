import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { PopoverController, NavParams } from '@ionic/angular';

import { Comic } from '../../comic';

@Component({
  selector: 'app-bookmark-actions',
  templateUrl: './bookmark-actions.component.html',
  styleUrls: [ './bookmark-actions.component.sass' ]
})
export class BookmarkActionsComponent {

  public comic: Comic;

  constructor(
    private popoverCtrl: PopoverController,
    private router: Router,
    private navParams: NavParams
  ) {
    this.comic = this.navParams.get('comic');
  }

  public goToVolume(comic: Comic): void {
    this.router.navigate([ '/issues', comic.publisher, comic.series, comic.volume ]);
    this.popoverCtrl.dismiss();
  }

  public goToLibrary(comic: Comic): void {
    this.router.navigate([ '/library/publishers', comic.publisher, 'series', comic.series, 'volumes' ]);
    this.popoverCtrl.dismiss();
  }
}
