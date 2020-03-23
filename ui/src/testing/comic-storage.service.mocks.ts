import { ComicStorageService } from '../app/comic-storage.service';

import { ComicFixtures } from './comic.fixtures';

export class ComicStorageServiceMocks {

  public static get comicStorageService(): jasmine.SpyObj<ComicStorageService> {
    return jasmine.createSpyObj('ComicStorageService', {
      get: Promise.resolve(ComicFixtures.comic),
      saveProgress: Promise.resolve(),
      getPageUrl: Promise.resolve('/api/read/923/0'),
      storeSurrounding: Promise.resolve({}),
      getBookmarks: Promise.resolve(ComicFixtures.volume),
      getFrontCoverThumbnail: Promise.resolve(''),
      deleteVolume: Promise.resolve(),
      saveIfStored: Promise.resolve()
    });
  }
}
