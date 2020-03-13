import { of } from 'rxjs';

import { SettingsService } from '../app/settings.service';

import { setting1 as setting } from './setting.fixtures';

export class SettingsServiceMocks {

  public static get settingsService(): jasmine.SpyObj<SettingsService> {
    const settingsService = jasmine.createSpyObj('SettingsService', ['list']);
    settingsService.list.and.returnValue(of([setting]));

    return settingsService;
  }
}
