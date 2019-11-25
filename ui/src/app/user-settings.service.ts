import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserSettingsService {

  private userSettings: { [key: string]: any; };

  constructor () {
    this.load();
  }

  get () {
    return this.userSettings;
  }

  save () {
    this.toggleDarkMode();
    localStorage.setItem('userSettings', JSON.stringify(this.userSettings));
  }

  load () {
    this.userSettings = JSON.parse(localStorage.getItem('userSettings')) || {};
    this.handleColorScheme();
  }

  private handleColorScheme () {
    if (!('darkMode' in this.userSettings)) {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)');
      this.userSettings.darkMode = prefersDark.matches;
      this.toggleDarkMode();
      prefersDark.addEventListener('change', (mediaQuery) => {
        this.userSettings.darkMode = mediaQuery.matches;
        this.save();
      });
    } else {
      this.toggleDarkMode();
    }
  }

  private toggleDarkMode () {
    document.body.classList.toggle('dark', this.userSettings.darkMode);
  }
}
