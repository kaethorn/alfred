import { TestBed } from '@angular/core/testing';

import { ComicFixtures } from '../testing/comic.fixtures';
import { ComicsServiceMocks } from '../testing/comics.service.mocks';
import { IndexedDbServiceMocks } from '../testing/indexed-db.service.mocks';

import { ComicDatabaseService } from './comic-database.service';
import { ComicsService } from './comics.service';
import { IndexedDbService } from './indexed-db.service';

let service: ComicDatabaseService;
let comicsService: jasmine.SpyObj<ComicsService>;
let indexedDbService: jasmine.SpyObj<IndexedDbService>;

describe('ComicDatabaseService', () => {

  beforeEach(() => {
    comicsService = ComicsServiceMocks.comicsService;
    indexedDbService = IndexedDbServiceMocks.IndexedDbService;

    TestBed.configureTestingModule({
      providers: [{
        provide: ComicsService, useValue: comicsService
      }, {
        provide: IndexedDbService, useValue: indexedDbService
      }]
    });
    service = TestBed.inject(ComicDatabaseService);
  });

  describe('#constructor', () => {

    beforeEach(() => {
      TestBed.resetTestingModule();
      indexedDbService = IndexedDbServiceMocks.IndexedDbService;
      TestBed.configureTestingModule({
        providers: [{
          provide: ComicsService, useValue: comicsService
        }, {
          provide: IndexedDbService, useValue: indexedDbService
        }]
      });
    });

    it('opens the DB', () => {
      service = TestBed.inject(ComicDatabaseService);
      expect(indexedDbService.open)
        .toHaveBeenCalledWith('Comics', 1, jasmine.any(Array));
    });

    describe('on success', () => {

      it('sets the ready flag', async () => {
        try {
          service = TestBed.inject(ComicDatabaseService);
          await new Promise(resolve => service.ready.subscribe(null, null, resolve));
        } catch (exception) {
        }
        expect(service.ready.hasError).toBeFalse();
      });
    });

    describe('on error', () => {

      beforeEach(() => {
        indexedDbService.open.and.callFake(() => {
          indexedDbService.ready.error(null);
        });
      });

      it('does not set the ready flag', async () => {
        try {
          service = TestBed.inject(ComicDatabaseService);
          await new Promise(resolve => service.ready.subscribe(null, resolve, null));
          await new Promise(resolve => setTimeout(resolve));
        } catch (exception) {
        }
        expect(service.ready.hasError).toBeTrue();
      });
    });
  });

  describe('#store', () => {

    describe('when not cached', () => {

      beforeEach(() => {
        indexedDbService.hasKey.and.resolveTo(false);
      });

      it('caches images', async () => {
        await service.store(ComicFixtures.comic);
        expect(comicsService.getPage).toHaveBeenCalledTimes(5);
      });
    });

    describe('when already cached', () => {

      beforeEach(() => {
        indexedDbService.hasKey.and.resolveTo(true);
      });

      it('does not cache again', async () => {
        await service.store(ComicFixtures.comic);
        expect(comicsService.getPage).not.toHaveBeenCalled();
      });
    });
  });
});
