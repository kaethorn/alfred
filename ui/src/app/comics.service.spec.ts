import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ComicsService } from './comics.service';

fdescribe('ComicsService', () => {
  let service: ComicsService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ]
    });
    service = TestBed.get(ComicsService);
    httpMock = TestBed.get(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    TestBed.resetTestingModule();
  });

  describe('#list', () => {

    beforeEach(function () {
      this.mockComics = {
        _embedded: {
          comics: [{
            path: '/batman 1.cbz',
            title: 'Batman One',
            series: 'Batman',
            number: 1,
            position: '1',
            volume: '1940',
            year: 1940,
            month: 4,
            publisher: 'DC Comics',
            pageCount: 20,
            thumbnail: '',
            _links: {
              self: {
                href: 'foo.bar/1'
              }
            }
          }, {
            path: '/batman 2.cbz',
            title: 'Batman Two',
            series: 'Batman',
            number: 2,
            position: '2',
            volume: '1940',
            year: 1940,
            month: 5,
            publisher: 'DC Comics',
            pageCount: 20,
            thumbnail: '',
            _links: {
              self: {
                href: 'foo.bar/2'
              }
            }
          }]
        },
      };
    });

    it('lists all comics', function () {
      service.list().subscribe(comics => {
        expect(comics.length).toBe(2);
        expect(comics[0].id).toEqual('1');
        expect(comics[0].path).toEqual('/batman 1.cbz');
        expect(comics[1].id).toEqual('2');
        expect(comics[1].path).toEqual('/batman 2.cbz');
      });

      const req = httpMock.expectOne('api/comics/search/findAllByOrderBySeriesAscVolumeAscPositionAsc');
      expect(req.request.method).toBe('GET');
      req.flush(this.mockComics);
    });
  });

  describe('#listByVolume', () => {

    beforeEach(function () {
      this.mockComics = {
        _embedded: {
          comics: [{
            path: '/batman 1.cbz',
            title: 'Batman One',
            series: 'Batman',
            number: 1,
            position: '1',
            volume: '1940',
            year: 1940,
            month: 4,
            publisher: 'DC Comics',
            pageCount: 20,
            thumbnail: '',
            _links: {
              self: {
                href: 'foo.bar/1'
              }
            }
          }, {
            path: '/batman 2.cbz',
            title: 'Batman Two',
            series: 'Batman',
            number: 2,
            position: '2',
            volume: '1940',
            year: 1940,
            month: 5,
            publisher: 'DC Comics',
            pageCount: 20,
            thumbnail: '',
            _links: {
              self: {
                href: 'foo.bar/2'
              }
            }
          }]
        },
      };
    });

    it('lists comics for the given volume', function () {
      service.listByVolume('DC Comics', 'Batman', '1940').subscribe(comics => {
        expect(comics.length).toBe(2);
        expect(comics[0].id).toEqual('1');
        expect(comics[0].path).toEqual('/batman 1.cbz');
        expect(comics[1].id).toEqual('2');
        expect(comics[1].path).toEqual('/batman 2.cbz');
      });

      const req = httpMock
        .expectOne('api/comics/search/findAllByPublisherAndSeriesAndVolumeOrderByPosition?publisher=DC%20Comics&series=Batman&volume=1940');
      expect(req.request.method).toBe('GET');
      req.flush(this.mockComics);
    });
  });
});
