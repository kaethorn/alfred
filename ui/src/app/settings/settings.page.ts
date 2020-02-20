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

  public settings: Setting[] = [];
  public updateError: any;
  public userSettings;

  constructor(
    private settingsService: SettingsService,
    private toastController: ToastController,
    private userSettingsService: UserSettingsService
  ) {
    this.userSettings = userSettingsService.get();
  }

  public ionViewWillEnter(): void {
    this.list();
  }

  public onSubmit(): void {
    for (const setting of this.settings) {
      this.settingsService.update(setting)
        .subscribe(
          () => this.showToast('Settings saved.'),
          error => this
            .showToast(`Error saving settings (${ error.status }: ${ error.statusText }).`, 5000)
        );
    }
  }

  public saveUserSettings(): void {
    this.userSettingsService.save();
  }

  private async showToast(message: string, duration = 3000): Promise<void> {
    const toast = await this.toastController.create({
      message,
      duration
    });
    toast.present();
  }

  private list(): void {
    this.settingsService.list()
      .subscribe((data: Setting[]) => {
        this.settings = data;
      });
  }
}
