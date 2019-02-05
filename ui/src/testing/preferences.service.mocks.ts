import { of } from 'rxjs';

import { PreferencesService } from '../app/preferences.service';

import { preference1 as preference } from './preference.fixtures';

const preferencesService = jasmine.createSpyObj('PreferencesService', ['list']);
preferencesService.list.and.returnValue( of([preference]) );

export { preferencesService as PreferencesServiceMocks };
