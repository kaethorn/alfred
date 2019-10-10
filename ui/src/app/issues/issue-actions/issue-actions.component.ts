import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { PopoverController, NavParams } from '@ionic/angular';

import { Comic } from '../../comic';

@Component({
  selector: 'app-issue-actions',
  templateUrl: './issue-actions.component.html',
  styleUrls: ['./issue-actions.component.sass'],
})
export class IssueActionsComponent {

  comic: Comic;

  constructor (
    private popoverCtrl: PopoverController,
    private router: Router,
    private navParams: NavParams
  ) {
    this.comic = this.navParams.get('comic');
  }

  download () {
    this.popoverCtrl.dismiss();
  }

  markAsReadUntil (comic: Comic): void {
    this.popoverCtrl.dismiss({ markAsReadUntil: comic });
  }

  goToLibrary (comic: Comic): void {
    this.router.navigate(['/library/publishers', comic.publisher, 'series', comic.series, 'volumes']);
    this.popoverCtrl.dismiss();
  }

  edit (comic: Comic): void {
    this.router.navigate(['/edit', comic.id]);
    this.popoverCtrl.dismiss();
  }
}
