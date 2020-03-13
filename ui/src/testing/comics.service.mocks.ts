import { of } from 'rxjs';

import { ComicsService } from '../app/comics.service';

import { comic1 as comic } from './comic.fixtures';

export class ComicsServiceMocks {

  public static get comicsService(): jasmine.SpyObj<ComicsService> {
    return jasmine.createSpyObj(ComicsService, {
      get: of(comic),
      list: of([comic]),
      listComicsWithErrors: of([comic]),
      listByVolume: of([comic]),
      listLastReadByVolume: of([]),
      update: of(comic),
      scrape: of(comic)
    });
  }
}
