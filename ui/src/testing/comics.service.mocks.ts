import { of } from 'rxjs';

import { ComicsService } from '../app/comics.service';

import { ComicFixtures } from './comic.fixtures';

export class ComicsServiceMocks {

  public static get comicsService(): jasmine.SpyObj<ComicsService> {
    const comicsService: jasmine.SpyObj<ComicsService> = jasmine.createSpyObj(ComicsService, {
      get: of(ComicFixtures.comic),
      list: of(ComicFixtures.volume),
      listComicsWithErrors: of(ComicFixtures.volume),
      listByVolume: of(ComicFixtures.volume),
      listLastReadByVolume: of(ComicFixtures.volume),
      update: of(ComicFixtures.comic),
      scrape: of(ComicFixtures.comic),
      listComicsWithoutErrors: of(ComicFixtures.volume),
      deletePage: of(null),
      fixIssue: of(null),
      markAsRead: of(ComicFixtures.comic),
      markAsUnread: of(ComicFixtures.comic),
      deleteComics: of(null),
      deleteProgress: of(null),
      deleteProgressForCurrentUser: of(null),
      bundleVolumes: of(null),
      getFirstByVolume: of(ComicFixtures.comic),
      getLastUnreadByVolume: of(ComicFixtures.comic),
      updateProgress: of(ComicFixtures.comic),
      getPage: of(null)
    });
    comicsService.markAsRead.and.callFake(c => of(Object.assign({}, c, { read: true })));
    comicsService.markAsUnread.and.callFake(c => of(Object.assign({}, c, { read: false })));

    return comicsService;
  }
}
