import { Component } from '@angular/core';
import { ToastController } from '@ionic/angular';

import { PreferencesService } from '../preferences.service';
import { Preference } from '../preference';

@Component({
  selector: 'app-preferences',
  templateUrl: './preferences.page.html',
  styleUrls: ['./preferences.page.sass']
})
export class PreferencesPage {

  constructor(
    private preferencesService: PreferencesService,
    private toastController: ToastController
  ) {
    this.list();
  }

  preferences: Preference[] = [];
  updateError: any;

  onSubmit () {
    for (const preference of this.preferences) {
      this.preferencesService.update(preference)
        .subscribe(
          () => this.showToast('Settings saved.'),
          (error) => this
            .showToast(`Error saving settings (${ error.status }: ${ error.statusText }).`, 5000)
        );
    }
  }

  private async showToast (message: string, duration: number = 3000) {
    const toast = await this.toastController.create({
      message: message,
      duration: duration
    });
    toast.present();
  }

  private list () {
    this.preferencesService.list()
      .subscribe((data: Preference[]) => {
        this.preferences = data;
      });
  }
}
