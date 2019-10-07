import { of } from 'rxjs';

import { comic1 as comic } from './comic.fixtures';

const comicsService = jasmine.createSpyObj('ComicsService', [
  'get',
  'list',
  'listComicsWithErrors',
  'listByVolume',
  'listLastReadByVolume',
  'update'
]);

comicsService.get.and.returnValue( of(comic) );
comicsService.list.and.returnValue( of([comic]) );
comicsService.listComicsWithErrors.and.returnValue( of([comic]) );
comicsService.listByVolume.and.returnValue( of([comic]) );
comicsService.listLastReadByVolume.and.returnValue( of([]) );
comicsService.update.and.returnValue( of(comic) );

export { comicsService as ComicsServiceMocks };
