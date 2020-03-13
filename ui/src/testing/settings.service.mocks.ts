import { of } from 'rxjs';

import { SettingsService } from '../app/settings.service';

import { setting1 as setting } from './setting.fixtures';

export class SettingsServiceMocks {

  public static get settingsService(): jasmine.SpyObj<SettingsService> {
    return jasmine.createSpyObj('SettingsService', {
      list: of([setting])
    });
  }
}
