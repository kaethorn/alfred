import { of } from 'rxjs';

import { ComicDatabaseService } from '../app/comic-database.service';

import { ComicFixtures } from './comic.fixtures';

export class ComicDatabaseServiceMocks {

  public static get comicDatabaseService(): jasmine.SpyObj<ComicDatabaseService> {
    const comicDatabaseService = jasmine.createSpyObj('ComicDatabaseService', {
      delete: Promise.resolve(),
      deleteAll: Promise.resolve(),
      getComic: Promise.resolve(ComicFixtures.comic),
      getComics: Promise.resolve([]),
      getComicsBy: Promise.resolve([]),
      getImageUrl: Promise.resolve('blob:http://localhost:4200/ed2dfdaa-b9b5-4e1a-b9af-28cbb34c4e4d'),
      isStored: Promise.resolve(true),
      ready: null,
      save: Promise.resolve(new Event('')),
      store: Promise.resolve()
    });
    comicDatabaseService.ready = of({ });

    return comicDatabaseService;
  }
}
