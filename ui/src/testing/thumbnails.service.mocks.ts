import { of } from 'rxjs';

import { ThumbnailsService } from '../app/thumbnails.service';

import { ThumbnailFixtures } from './thumbnail.fixtures';

export class ThumbnailsServiceMocks {

  public static get thumbnailsService(): jasmine.SpyObj<ThumbnailsService> {
    return jasmine.createSpyObj('ThumbnailsService', {
      getFrontCover: of(ThumbnailFixtures.thumbnail),
      getBackCover: of(ThumbnailFixtures.thumbnail)
    });
  }
}
