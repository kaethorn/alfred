import { of } from 'rxjs';

import { ThumbnailsService } from '../app/thumbnails.service';

import { thumbnail1 as thumbnail } from './thumbnail.fixtures';

export class ThumbnailsServiceMocks {

  public static get thumbnailsService(): jasmine.SpyObj<ThumbnailsService> {
    return jasmine.createSpyObj('ThumbnailsService', {
      getFrontCover: of(thumbnail),
      getBackCover: of(thumbnail)
    });
  }
}
