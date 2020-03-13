import { of } from 'rxjs';

import { ComicsService } from '../app/comics.service';

import { comic1 as comic } from './comic.fixtures';

export class ComicsServiceMocks {

  public static get comicsService(): jasmine.SpyObj<ComicsService> {
    const comicsService = jasmine.createSpyObj(ComicsService, [
      'get',
      'list',
      'listComicsWithErrors',
      'listByVolume',
      'listLastReadByVolume',
      'update',
      'scrape'
    ]);

    comicsService.get.and.returnValue(of(comic));
    comicsService.list.and.returnValue(of([comic]));
    comicsService.listComicsWithErrors.and.returnValue(of([comic]));
    comicsService.listByVolume.and.returnValue(of([comic]));
    comicsService.listLastReadByVolume.and.returnValue(of([]));
    comicsService.update.and.returnValue(of(comic));
    comicsService.scrape.and.returnValue(of(comic));

    return comicsService;
  }
}
