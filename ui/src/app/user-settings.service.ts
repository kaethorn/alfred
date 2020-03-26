import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserSettingsService {

  private userSettings: { [key: string]: any } = {};

  constructor() { }

  public get(): { [key: string]: any } {
    return this.userSettings;
  }

  public save(): void {
    this.toggleColorScheme();
    localStorage.setItem('userSettings', JSON.stringify(this.userSettings));
  }

  public load(): void {
    this.userSettings = JSON.parse(localStorage.getItem('userSettings')) || {};
    this.handleColorScheme();
  }

  private handleColorScheme(): void {
    if (!('darkMode' in this.userSettings)) {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)');
      this.userSettings.darkMode = prefersDark.matches;
      this.toggleColorScheme();
      prefersDark.addEventListener('change', mediaQuery => {
        this.userSettings.darkMode = mediaQuery.matches;
        this.save();
      });
    } else {
      this.toggleColorScheme();
    }
  }

  private toggleColorScheme(): void {
    document.body.classList.toggle('dark', this.userSettings.darkMode);
  }
}
