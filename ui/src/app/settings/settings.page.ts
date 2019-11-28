import { Component } from '@angular/core';
import { ToastController } from '@ionic/angular';

import { SettingsService } from '../settings.service';
import { Setting } from '../setting';
import { UserSettingsService } from '../user-settings.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.page.html',
  styleUrls: ['./settings.page.sass']
})
export class SettingsPage {

  settings: Setting[] = [];
  updateError: any;
  userSettings;

  constructor (
    private settingsService: SettingsService,
    private toastController: ToastController,
    private userSettingsService: UserSettingsService,
  ) {
    this.userSettings = userSettingsService.get();
  }

  ionViewWillEnter () {
    this.list();
  }

  onSubmit () {
    for (const setting of this.settings) {
      this.settingsService.update(setting)
        .subscribe(
          () => this.showToast('Settings saved.'),
          (error) => this
            .showToast(`Error saving settings (${ error.status }: ${ error.statusText }).`, 5000)
        );
    }
  }

  saveUserSettings () {
    this.userSettingsService.save();
  }

  private async showToast (message: string, duration: number = 3000) {
    const toast = await this.toastController.create({
      message,
      duration
    });
    toast.present();
  }

  private list () {
    this.settingsService.list()
      .subscribe((data: Setting[]) => {
        this.settings = data;
      });
  }
}
