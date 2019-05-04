import { of } from 'rxjs';

import { setting1 as setting } from './setting.fixtures';

const settingsService = jasmine.createSpyObj('SettingsService', ['list']);
settingsService.list.and.returnValue( of([setting]) );

export { settingsService as SettingsServiceMocks };
