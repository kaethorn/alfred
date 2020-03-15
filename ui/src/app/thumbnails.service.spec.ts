import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ComicFixtures } from '../testing/comic.fixtures';
import { ThumbnailFixtures } from '../testing/thumbnail.fixtures';

import { ThumbnailsService } from './thumbnails.service';

let service: ThumbnailsService;
let httpMock: HttpTestingController;

describe('ThumbnailsService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ]
    });
    service = TestBed.inject(ThumbnailsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    TestBed.resetTestingModule();
  });

  describe('#get', () => {

    it('returns a sanitized URL', () => {
      service.getFrontCover(ComicFixtures.comic.id).subscribe(thumbnail => {
        expect((thumbnail.url as any).changingThisBreaksApplicationSecurity).toContain('abcedf1234');
      });
      const req = httpMock.expectOne(`/api/thumbnails/front-cover/${ ComicFixtures.comic.id }`);
      expect(req.request.method).toBe('GET');
      req.flush(ThumbnailFixtures.thumbnail);
    });
  });
});
