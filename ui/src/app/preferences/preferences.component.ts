import { Component, OnInit } from '@angular/core';

import { PreferencesService } from '../preferences.service';
import { Preference } from '../preference';

@Component({
  selector: 'app-preferences',
  templateUrl: './preferences.component.html',
  styleUrls: ['./preferences.component.sass']
})
export class PreferencesComponent implements OnInit {

  constructor(
    private preferencesService: PreferencesService
  ) {
    this.list();
  }

  ngOnInit() {
  }

  preferences : Preference[] = [];
  updateError : any;

  onSubmit () {
    for (let preference of this.preferences) {
      this.preferencesService.update(preference)
        .subscribe(
          () => {},
          (error) => this.onUpdateError(error)
        );
    }
  }

  private onUpdateError (error) {
    this.updateError = error;
  }

  private list () {
    this.preferencesService.list()
      .subscribe((data: Preference[]) => {
        this.preferences = data;
      });
  }
}
