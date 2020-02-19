import { of } from 'rxjs';

import { thumbnail1 as thumbnail } from './thumbnail.fixtures';

const thumbnailsService = jasmine.createSpyObj('ThumbnailsService', [
  'getFrontCover',
  'getBackCover'
]);

thumbnailsService.getFrontCover.and.returnValue( of(thumbnail) );
thumbnailsService.getBackCover.and.returnValue( of(thumbnail) );

export { thumbnailsService as ThumbnailsServiceMocks };
