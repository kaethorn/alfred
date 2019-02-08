import { of } from 'rxjs';

import { ComicsService } from '../app/comics.service';

import { comic1 as comic } from './comic.fixtures';

const comicsService = jasmine.createSpyObj('ComicsService', [
  'listByVolume',
  'listVolumesByPublisher',
  'get',
  'listLastReadByVolume',
  'findVolumesBySeriesAndPublishers'
]);
comicsService.listByVolume.and.returnValue( of([comic]) );
comicsService.get.and.returnValue( of(comic) );
comicsService.listVolumesByPublisher.and.returnValue( of([]) );
comicsService.listLastReadByVolume.and.returnValue( of([]) );
comicsService.findVolumesBySeriesAndPublishers.and.returnValue( of([]) );

export { comicsService as ComicsServiceMocks };
