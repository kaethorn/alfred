import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { PopoverController, NavParams } from '@ionic/angular';

import { Comic } from '../../comic';

@Component({
  selector: 'app-issue-actions',
  templateUrl: './issue-actions.component.html',
  styleUrls: [ './issue-actions.component.sass' ]
})
export class IssueActionsComponent {

  public comic: Comic;

  constructor(
    private popoverCtrl: PopoverController,
    private router: Router,
    private navParams: NavParams
  ) {
    this.comic = this.navParams.get('comic');
  }

  public download(): void {
    this.popoverCtrl.dismiss();
  }

  public markAsReadUntil(comic: Comic): void {
    this.popoverCtrl.dismiss({ markAsReadUntil: comic });
  }

  public goToLibrary(comic: Comic): void {
    this.router.navigate([ '/library/publishers', comic.publisher, 'series', comic.series, 'volumes' ]);
    this.popoverCtrl.dismiss();
  }

  public edit(comic: Comic): void {
    this.router.navigate([ '/edit', comic.id ]);
    this.popoverCtrl.dismiss();
  }
}
