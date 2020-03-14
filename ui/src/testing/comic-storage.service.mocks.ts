import { ComicStorageService } from '../app/comic-storage.service';

import { comic1 as comic } from './comic.fixtures';

export class ComicStorageServiceMocks {

  public static get comicStorageService(): jasmine.SpyObj<ComicStorageService> {
    return jasmine.createSpyObj('ComicStorageService', {
      get: Promise.resolve(Object.assign({}, comic)),
      saveProgress: Promise.resolve(),
      getPageUrl: Promise.resolve('/api/read/923/0'),
      storeSurrounding: Promise.resolve({}),
      getBookmarks: Promise.resolve([comic]),
      getFrontCoverThumbnail: Promise.resolve(''),
      deleteVolume: Promise.resolve()
    });
  }
}
