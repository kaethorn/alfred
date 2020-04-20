/* eslint-disable no-console */

import { Injectable, ApplicationRef } from '@angular/core';
import { SwUpdate } from '@angular/service-worker';
import { AlertController } from '@ionic/angular';
import { interval, concat } from 'rxjs';
import { first } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UpdateService {

  constructor(
    private appRef: ApplicationRef,
    private updates: SwUpdate,
    private alertController: AlertController
  ) { }

  public start(): void {
    this.updates.available.subscribe(event => {
      console.log('current version is', event.current);
      console.log('available version is', event.available);
    });
    this.updates.activated.subscribe(event => {
      console.log('old version was', event.previous);
      console.log('new version is', event.current);
    });
    this.updates.available.subscribe(event => {
      this.presentAlert(event.available.hash);
    });
    this.checkForUpdates();
  }

  private checkForUpdates(): void {
    // Allow the app to stabilize first, before starting polling for updates with `interval()`.
    const appIsStable$ = this.appRef.isStable.pipe(first(isStable => isStable === true));
    const everySixHours$ = interval(6 * 60 * 60 * 1000);
    const everySixHoursOnceAppIsStable$ = concat(appIsStable$, everySixHours$);

    everySixHoursOnceAppIsStable$.subscribe(() => {
      if (this.updates.isEnabled) {
        this.updates.checkForUpdate();
      }
    });
  }

  private async presentAlert(version: string): Promise<void> {
    const alert = await this.alertController.create({
      buttons: [
        {
          cssClass: 'secondary',
          role: 'cancel',
          text: 'Cancel'
        }, {
          handler: (): void => {
            this.updates.activateUpdate().then(() => document.location.reload());
          },
          text: 'Update'
        }
      ],
      header: 'Update',
      message: `Version <strong>${ version } </strong> is available.`
    });

    await alert.present();
  }
}
