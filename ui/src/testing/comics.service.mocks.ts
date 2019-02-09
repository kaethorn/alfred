import { of } from 'rxjs';

import { comic1 as comic } from './comic.fixtures';

const comicsService = jasmine.createSpyObj('ComicsService', [
  'findVolumesBySeriesAndPublishers',
  'get',
  'listByVolume',
  'listLastReadByVolume',
  'listVolumesByPublisher',
  'update'
]);

comicsService.findVolumesBySeriesAndPublishers.and.returnValue( of([]) );
comicsService.get.and.returnValue( of(comic) );
comicsService.listByVolume.and.returnValue( of([comic]) );
comicsService.listLastReadByVolume.and.returnValue( of([]) );
comicsService.listVolumesByPublisher.and.returnValue( of([]) );
comicsService.update.and.returnValue( of(comic) );

export { comicsService as ComicsServiceMocks };
