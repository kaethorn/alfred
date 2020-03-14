import { of } from 'rxjs';

import { ComicDatabaseService } from '../app/comic-database.service';

export class ComicDatabaseServiceMocks {

  public static get comicDatabaseService(): jasmine.SpyObj<ComicDatabaseService> {
    const comicDatabaseService = jasmine.createSpyObj('ComicDatabaseService', {
      store: Promise.resolve(),
      delete: Promise.resolve(),
      getComics: Promise.resolve([]),
      ready: null,
      isStored: Promise.resolve(true),
      getComicsBy: Promise.resolve([])
    });
    comicDatabaseService.ready = of({ });

    return comicDatabaseService;
  }
}
