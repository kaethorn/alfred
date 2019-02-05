import { of } from 'rxjs';

import { ComicsService } from '../app/comics.service';

import { comic1 as comic } from './comic.fixtures';

const comicsService = jasmine.createSpyObj('ComicsService', ['listByVolume', 'listVolumesByPublisher', 'get']);
comicsService.listByVolume.and.returnValue( of([comic]) );
comicsService.get.and.returnValue( of(comic) );
comicsService.listVolumesByPublisher.and.returnValue( of([]) );

export { comicsService as ComicsServiceMocks };
