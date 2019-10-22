import { TestBed } from '@angular/core/testing';

import { throwError, of } from 'rxjs';

import { ComicsService } from './comics.service';
import { ComicStorageService } from './comic-storage.service';
import { ComicsServiceMocks as comicsService } from '../testing/comics.service.mocks';
import { volume, volumeInProgress, volumesInProgress } from '../testing/comic.fixtures';
import { ComicDatabaseService } from './comic-database.service';
import { ThumbnailsService } from './thumbnails.service';

describe('ComicStorageService', () => {
  let service: ComicStorageService;
  const comicDatabaseService = jasmine
    .createSpyObj('ComicDatabaseService', [ 'store', 'delete', 'getComics', 'ready', 'getComicsBy' ]);

  beforeEach(() => {
    comicDatabaseService.store.and.returnValue(Promise.resolve());
    comicDatabaseService.delete.and.returnValue(Promise.resolve());
    comicDatabaseService.getComicsBy.and.returnValue(Promise.resolve([]));
    comicDatabaseService.getComics.and.returnValue(Promise.resolve([]));
    comicDatabaseService.ready = of({ });

    TestBed.configureTestingModule({
      providers: [{
        provide: ComicsService, useValue: comicsService
      }, {
        provide: ComicDatabaseService, useValue: comicDatabaseService
      }, {
        provide: ThumbnailsService, useValue: {}
      }],
    });
    service = TestBed.get(ComicStorageService);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  describe('#getBookmarks', () => {

    describe('when offline', () => {

      beforeEach(() => {
        comicsService.listLastReadByVolume.and.returnValue(throwError('Offline'));
      });

      describe('with synced comics', () => {

        beforeEach(() => {
          comicDatabaseService.getComics.and.returnValue(Promise.resolve(volumesInProgress));
        });

        it('filters offline comics', async () => {
          const comics = await service.getBookmarks();
          expect(comics.length).toBe(2);
          expect(comics[0].id).toEqual('13');
          expect(comics[1].id).toEqual('22');
        });
      });

      describe('without synced comics', () => {

        beforeEach(() => {
          comicDatabaseService.getComics.and.returnValue(Promise.resolve([]));
        });

        it('filters offline comics', async () => {
          const comics = await service.getBookmarks();
          expect(comics.length).toBe(0);
        });
      });
    });
  });

  describe('#storeSurrounding', () => {

    beforeEach(() => {
      spyOn(service, 'get').and.callFake((comicId: string) => {
        return Promise.resolve(volume.find(comic => comic.id === comicId));
      });
      comicDatabaseService.getComics.and.returnValue(Promise.resolve(
        volume.slice(1, 5)
      ));
    });

    it('stores the previous comic', async () => {
      // Given a volume of 8 comics, where no. 2 is the cache reference:
      // 0. uncached
      // 1. cached
      // 2. cached
      // 3. cached
      // 4. cached
      // 5. cached
      // 6. uncached
      // 7. uncached
      //
      // Calling this method on no. 3 will result in the following scenario:
      // 0. uncached
      // 1. uncached
      // 2. cached
      // 3. cached
      // 4. cached
      // 5. cached
      // 6. cached
      // 7. uncached

      await service.storeSurrounding(volume[3].id);
      expect(comicDatabaseService.store).not.toHaveBeenCalledWith(volume[0]);
      expect(comicDatabaseService.store).not.toHaveBeenCalledWith(volume[1]);
      expect(comicDatabaseService.store).toHaveBeenCalledWith(volume[2]);
      expect(comicDatabaseService.store).toHaveBeenCalledWith(volume[3]);
      expect(comicDatabaseService.store).toHaveBeenCalledWith(volume[4]);
      expect(comicDatabaseService.store).toHaveBeenCalledWith(volume[5]);
      expect(comicDatabaseService.store).toHaveBeenCalledWith(volume[6]);
      expect(comicDatabaseService.store).not.toHaveBeenCalledWith(volume[7]);

      expect(comicDatabaseService.delete).not.toHaveBeenCalledWith(volume[0]);
      expect(comicDatabaseService.delete).toHaveBeenCalledWith(volume[1]);
      expect(comicDatabaseService.delete).not.toHaveBeenCalledWith(volume[2]);
      expect(comicDatabaseService.delete).not.toHaveBeenCalledWith(volume[3]);
      expect(comicDatabaseService.delete).not.toHaveBeenCalledWith(volume[4]);
      expect(comicDatabaseService.delete).not.toHaveBeenCalledWith(volume[5]);
      expect(comicDatabaseService.delete).not.toHaveBeenCalledWith(volume[6]);
      expect(comicDatabaseService.delete).not.toHaveBeenCalledWith(volume[7]);

      // TODO test edge cases (start or beginning of volume)
    });
  });
});
