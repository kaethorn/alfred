import { TestBed } from '@angular/core/testing';

import { of, throwError, Observable, Subject } from 'rxjs';

import { comic1 as comic } from '../testing/comic.fixtures';
import { QueueService } from './queue.service';
import { ComicsService } from './comics.service';
import { Comic } from './comic';

describe('QueueService', () => {
  let service: QueueService;
  const comicsService = jasmine.createSpyObj('ComicsService', [ 'update' ]);
  const updateSpy: jasmine.Spy = comicsService.update;

  beforeEach(() => {
    updateSpy.and.returnValue(of(comic));
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [{
        provide: ComicsService, useValue: comicsService
      }],
    });
    service = TestBed.get(QueueService);
  });

  afterEach(() => {
    updateSpy.calls.reset();
    TestBed.resetTestingModule();
  });

  describe('#hasItems', () => {

    it('reflects the state of the internal queue', () => {
      expect(service.hasItems()).toBe(false);
      service.load();
      expect(service.hasItems()).toBe(false);
      service.add({ id: '123' } as Comic);
      expect(service.hasItems()).toBe(true);
    });
  });

  describe('#process', () => {

    describe('without items in the queue', () => {

      it('completes', () => {
        service.process().subscribe(() => {
          expect(true).toBe(true);
        }, () => {
          expect(false).toBe(true);
        }, () => {
          expect(true).toBe(true);
        });
      });
    });

    describe('with items', () => {

      beforeEach(() => {
        localStorage.setItem('queue', JSON.stringify({
          1: { path: 'one' },
          2: { path: 'two' },
        }));
        service.load();
      });

      describe('on error', () => {

        beforeEach(() => {
          updateSpy.and.returnValue( throwError(comic) );
        });

        it('does not complete', (done) => {
          service.process().subscribe(() => {
          }, () => {
            expect(updateSpy.calls.count()).toBe(1);
            expect(service.count()).toBe(2);
            done();
          });
        });
      });

      describe('on success', () => {

        beforeEach(() => {
        });

        it('completes', (done) => {
          service.process().subscribe(() => {
          }, () => {
          }, () => {
            expect(updateSpy.calls.count()).toBe(2);
            expect(comicsService.update).toHaveBeenCalledWith({ path: 'one' });
            expect(comicsService.update).toHaveBeenCalledWith({ path: 'two' });
            expect(service.count()).toBe(0);
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
          }, () => {
            expect(updateSpy.calls.count()).toBe(2);
            expect(service.count()).toBe(1);
            done();
          });
        });
      });
    });
  });
});
