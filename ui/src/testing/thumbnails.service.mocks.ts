import { of } from 'rxjs';

import { ThumbnailsService } from '../app/thumbnails.service';

import { thumbnail1 as thumbnail } from './thumbnail.fixtures';

export class ThumbnailsServiceMocks {

  public static get thumbnailsService(): jasmine.SpyObj<ThumbnailsService> {
    const thumbnailsService = jasmine.createSpyObj('ThumbnailsService', [
      'getFrontCover',
      'getBackCover'
    ]);

    thumbnailsService.getFrontCover.and.returnValue(of(thumbnail));
    thumbnailsService.getBackCover.and.returnValue(of(thumbnail));

    return thumbnailsService;
  }
}
