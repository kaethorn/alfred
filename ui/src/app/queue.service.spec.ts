import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { ComicFixtures } from '../testing/comic.fixtures';

import { Comic } from './comic';
import { ComicDatabaseService } from './comic-database.service';
import { ComicsService } from './comics.service';
import { QueueService } from './queue.service';

let service: QueueService;
let dbService: ComicDatabaseService;

const comicsService = jasmine.createSpyObj('ComicsService', [ 'updateProgress' ]);
const updateProgressSpy: jasmine.Spy = comicsService.updateProgress;

describe('QueueService', () => {

  beforeEach(async () => {
    updateProgressSpy.and.returnValue(of(ComicFixtures.comic));
    TestBed.configureTestingModule({
      providers: [{
        provide: ComicsService, useValue: comicsService
      }]
    });
    dbService = TestBed.inject(ComicDatabaseService);
    await dbService.ready.toPromise();
    service = TestBed.inject(QueueService);
  });

  afterEach(async done => {
    updateProgressSpy.calls.reset();
    TestBed.resetTestingModule();
    await dbService.deleteAll();
    done();
  });

  describe('#hasItems', () => {

    it('reflects the state of the internal queue', async () => {
      expect(await service.hasItems()).toBe(false);
      service.add({ id: '123' } as Comic);
      expect(await service.hasItems()).toBe(true);
    });
  });

  describe('#process', () => {

    describe('without items in the queue', () => {

      it('completes', done => {
        service.process().subscribe(() => {
          expect(true).toBe(true);
        }, () => {
          expect(false).toBe(true);
          done();
        }, () => {
          expect(true).toBe(true);
          done();
        });
      });
    });

    describe('with items', () => {

      beforeEach(async done => {
        await service.add({ id: 'one' } as Comic);
        await service.add({ id: 'two' } as Comic);
        done();
      });

      describe('on error', () => {

        beforeEach(() => {
          updateProgressSpy.and.returnValue(throwError(ComicFixtures.comic));
        });

        it('does not complete', done => {
          service.process().subscribe(() => {
          }, async () => {
            expect(updateProgressSpy.calls.count()).toBe(1);
            expect(await service.count()).toBe(2);
            done();
          });
        });
      });

      describe('on success', () => {

        it('completes', done => {
          service.process().subscribe(() => {
          }, () => {
          }, async () => {
            expect(updateProgressSpy.calls.count()).toBe(2);
            expect(comicsService.updateProgress).toHaveBeenCalledWith({ id: 'one' });
            expect(comicsService.updateProgress).toHaveBeenCalledWith({ id: 'two' });
            expect(await service.count()).toBe(0);
            done();
          });
        });
      });

      describe('on partial success', () => {

        beforeEach(() => {
          let index = 0;
          updateProgressSpy.and.callFake(() => {
            if (index > 0) {
              return throwError(ComicFixtures.comic);
            }
            index += 1;
            return of(ComicFixtures.comic);
          });
        });

        it('does not complete', done => {
          service.process().subscribe(() => {
          }, async () => {
            expect(updateProgressSpy.calls.count()).toBe(2);
            expect(await service.count()).toBe(1);
            done();
          });
        });
      });
    });
  });
});
