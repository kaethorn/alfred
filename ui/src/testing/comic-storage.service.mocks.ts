import { ComicStorageService } from '../app/comic-storage.service';

import { comic1 as comic } from './comic.fixtures';

export class ComicStorageServiceMocks {

  public static get comicStorageService(): jasmine.SpyObj<ComicStorageService> {
    const comicStorageService = jasmine
      .createSpyObj('ComicStorageService', ['get', 'saveProgress', 'getPageUrl', 'storeSurrounding']);
    comicStorageService.get.and.returnValue(Promise.resolve(Object.assign({}, comic)));
    comicStorageService.saveProgress.and.returnValue(Promise.resolve());
    comicStorageService.getPageUrl.and.returnValue(Promise.resolve('/api/read/923/0'));
    comicStorageService.storeSurrounding.and.returnValue(Promise.resolve({}));

    return comicStorageService;
  }
}
