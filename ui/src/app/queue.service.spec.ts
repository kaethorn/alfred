import { TestBed } from '@angular/core/testing';

import { of, throwError } from 'rxjs';

import { comic1 as comic } from '../testing/comic.fixtures';
import { QueueService } from './queue.service';
import { ComicsService } from './comics.service';
import { Comic } from './comic';
import { ComicDatabaseService } from './comic-database.service';

describe('QueueService', () => {
  let service: QueueService;
  let dbService: ComicDatabaseService;
  const comicsService = jasmine.createSpyObj('ComicsService', [ 'update' ]);
  const updateSpy: jasmine.Spy = comicsService.update;

  beforeEach(async () => {
    updateSpy.and.returnValue(of(comic));
    TestBed.configureTestingModule({
      providers: [{
        provide: ComicsService, useValue: comicsService
      }],
    });
    dbService = TestBed.get(ComicDatabaseService);
    await dbService.ready.toPromise();
    service = TestBed.get(QueueService);
  });

  afterEach(async (done) => {
    updateSpy.calls.reset();
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

      it('completes', (done) => {
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

      beforeEach(async (done) => {
        await service.add({ id: 'one' } as Comic);
        await service.add({ id: 'two' } as Comic);
        done();
      });

      describe('on error', () => {

        beforeEach(() => {
          updateSpy.and.returnValue( throwError(comic) );
        });

        it('does not complete', (done) => {
          service.process().subscribe(() => {
          }, async () => {
            expect(updateSpy.calls.count()).toBe(1);
            expect(await service.count()).toBe(2);
            done();
          });
        });
      });

      describe('on success', () => {

        it('completes', (done) => {
          service.process().subscribe(() => {
          }, () => {
          }, async () => {
            expect(updateSpy.calls.count()).toBe(2);
            expect(comicsService.update).toHaveBeenCalledWith({ id: 'one' });
            expect(comicsService.update).toHaveBeenCalledWith({ id: 'two' });
            expect(await service.count()).toBe(0);
            done();
          });
        });
      });

      describe('on partial success', () => {

        beforeEach(() => {
          let index = 0;
          updateSpy.and.callFake(() => {
            if (index > 0) {
              return throwError(comic);
            }
            index += 1;
            return of(comic);
          });
        });

        it('does not complete', (done) => {
          service.process().subscribe(() => {
          }, async () => {
            expect(updateSpy.calls.count()).toBe(2);
            expect(await service.count()).toBe(1);
            done();
          });
        });
      });
    });
  });
});
