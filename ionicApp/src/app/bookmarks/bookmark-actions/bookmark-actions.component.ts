import { Component } from '@angular/core';
import { PopoverController, NavParams } from '@ionic/angular';

import { Comic } from '../../comic';

@Component({
  selector: 'app-bookmark-actions',
  templateUrl: './bookmark-actions.component.html',
  styleUrls: ['./bookmark-actions.component.sass'],
})
export class BookmarkActionsComponent {

  comic: Comic;

  constructor(
    private popoverCtrl: PopoverController,
    private navParams: NavParams
  ) {
    this.comic = navParams.get('comic');
  }
}
