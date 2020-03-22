import { TestBed } from '@angular/core/testing';

import { ComicsServiceMocks } from '../testing/comics.service.mocks';

import { ComicDatabaseService } from './comic-database.service';
import { ComicsService } from './comics.service';
import { IndexedDb } from './indexed-db';

let service: ComicDatabaseService;
let comicsService: jasmine.SpyObj<ComicsService>;
let dbOpenSpy: jasmine.Spy;

fdescribe('ComicDatabaseService', () => {

  beforeEach(() => {
    comicsService = ComicsServiceMocks.comicsService;
    dbOpenSpy = spyOn(IndexedDb.prototype, 'open').and.callFake(function() {
      this.ready.complete();
    });

    TestBed.configureTestingModule({
      providers: [{
        provide: ComicsService, useValue: comicsService
      }]
    });
  });

  describe('#constructor', () => {

    it('opens the DB', () => {
      service = TestBed.inject(ComicDatabaseService);
      expect(IndexedDb.prototype.open)
        .toHaveBeenCalledWith('Comics', 1, jasmine.any(Array), jasmine.any(IDBFactory));
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
        dbOpenSpy.and.callFake(function() {
          this.ready.error(null);
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
});
