import { of } from 'rxjs';

import { ThumbnailsService } from '../app/thumbnails.service';

import { ThumbnailFixtures } from './thumbnail.fixtures';

export class ThumbnailsServiceMocks {

  public static get thumbnailsService(): jasmine.SpyObj<ThumbnailsService> {
    return jasmine.createSpyObj('ThumbnailsService', {
      getBackCover: of(ThumbnailFixtures.thumbnail),
      getFrontCover: of(ThumbnailFixtures.thumbnail)
    });
  }
}
