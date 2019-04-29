import { of } from 'rxjs';

import { preference1 as preference } from './preference.fixtures';

const preferencesService = jasmine.createSpyObj('PreferencesService', ['list']);
preferencesService.list.and.returnValue( of([preference]) );

export { preferencesService as PreferencesServiceMocks };
