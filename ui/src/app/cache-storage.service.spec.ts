import { TestBed } from '@angular/core/testing';

import { CacheStorageMocks } from '../testing/cache-storage.mocks';
import { ComicFixtures } from '../testing/comic.fixtures';

import { CacheStorageService } from './cache-storage.service';
import { CACHES_TOKEN } from './caches.token';

let service: CacheStorageService;
let caches: jasmine.SpyObj<CacheStorage>;
let cache: jasmine.SpyObj<Cache>;

describe('CacheStorageService', () => {

  beforeEach(() => {
    caches = CacheStorageMocks.cacheStorage;
    cache = CacheStorageMocks.cache;
    caches.open.and.resolveTo(cache);

    TestBed.configureTestingModule({
      providers: [{
        provide: CACHES_TOKEN, useValue: caches
      }]
    });
    service = TestBed.inject(CacheStorageService);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  describe('#resetThumbnailsCache', () => {

    it('removes all cached thumbnails for the given comic ID', async () => {
      await service.resetThumbnailsCache(ComicFixtures.comic.id);
      expect(caches.keys).toHaveBeenCalled();
      expect(caches.open).toHaveBeenCalled();
      expect(cache.keys).toHaveBeenCalled();
    });
  });
});
