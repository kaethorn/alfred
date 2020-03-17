import { of } from 'rxjs';

import { ComicDatabaseService } from '../app/comic-database.service';

import { ComicFixtures } from './comic.fixtures';

export class ComicDatabaseServiceMocks {

  public static get comicDatabaseService(): jasmine.SpyObj<ComicDatabaseService> {
    const comicDatabaseService = jasmine.createSpyObj('ComicDatabaseService', {
      getComic: Promise.resolve(ComicFixtures.comic),
      store: Promise.resolve(),
      delete: Promise.resolve(),
      getComics: Promise.resolve([]),
      ready: null,
      isStored: Promise.resolve(true),
      getComicsBy: Promise.resolve([]),
      deleteAll: Promise.resolve(),
      save: Promise.resolve(new Event('')),
      getImageUrl: Promise.resolve('blob:http://localhost:4200/ed2dfdaa-b9b5-4e1a-b9af-28cbb34c4e4d')
    });
    comicDatabaseService.ready = of({ });

    return comicDatabaseService;
  }
}
