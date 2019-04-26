import { Component, OnInit } from '@angular/core';
import { PopoverController, NavParams } from '@ionic/angular';

import { Comic } from '../../comic';

@Component({
  selector: 'app-bookmark-actions-component',
  templateUrl: './bookmark-actions-component.component.html',
  styleUrls: ['./bookmark-actions-component.component.sass'],
})
export class BookmarkActionsComponentComponent implements OnInit {

  comic: Comic;

  constructor(
    private popoverCtrl: PopoverController,
    private navParams: NavParams
  ) {
    this.comic = navParams.get('comic');
  }

  ngOnInit() {}
}
