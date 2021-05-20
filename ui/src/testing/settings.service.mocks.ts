import { of } from 'rxjs';

import { SettingsService } from '../app/settings.service';

import { SettingFixtures } from './setting.fixtures';

export class SettingsServiceMocks {

  public static get settingsService(): jasmine.SpyObj<SettingsService> {
    return jasmine.createSpyObj('SettingsService', {
      get: of(SettingFixtures.setting),
      list: of([ SettingFixtures.setting ]),
      update: of(SettingFixtures.setting)
    });
  }
}
